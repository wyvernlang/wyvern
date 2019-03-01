/** @author vzhao */

package wyvern.target.corewyvernIL.decltype;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import wyvern.stdlib.support.backend.BytecodeOuterClass;
import wyvern.target.corewyvernIL.IASTNode;
import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.effects.Effect;
import wyvern.target.corewyvernIL.effects.EffectSet;
import wyvern.target.corewyvernIL.support.FailureReason;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.support.View;
import wyvern.tools.errors.FileLocation;

/* TODO: adapt(), doAvoid() */
public class EffectDeclType extends DeclType implements IASTNode {
    private EffectSet effectSet;

    public EffectDeclType(String name, EffectSet effectSet, FileLocation loc, EffectSet lb, EffectSet ub) {
        super(name);
        this.effectSet = effectSet;
        this.lowerBound = lb;
        this.upperBound = ub;
    }

    public EffectDeclType(String name, EffectSet effectSet, FileLocation loc) {
        super(name);
        this.effectSet = effectSet;
    }

    private EffectSet lowerBound;
    private EffectSet upperBound;

    public EffectSet getLowerBound() {
        return lowerBound;
    }

    @Override
    public <S, T> T acceptVisitor(ASTVisitor<S, T> visitor, S state) {
        return visitor.visit(state, this); // could return null here, though implication is unknown
    }

    public EffectSet getEffectSet() {
        return effectSet;
    }

    @Override
    public BytecodeOuterClass.DeclType emitBytecode() {
        // stub
        return BytecodeOuterClass.DeclType.newBuilder().setOpaqueTypeDecl(
                BytecodeOuterClass.DeclType.OpaqueTypeDecl.newBuilder().setName(getName()).setStateful(false)
        ).build();
    }

    /** Conduct semantics check, by decomposing the effect sets of the two
     * (effect)DeclTypes before comparing them.
     */
    @Override
    public boolean isSubtypeOf(DeclType dt, TypeContext ctx, FailureReason reason) {
        // TODO: instead of the code below, implement semantics comparison
        if (!(dt instanceof EffectDeclType)) {
            return false;
        }
        EffectDeclType edt = (EffectDeclType) dt;

        /* edt == type or method annotations, vs.
         * this == module def or method calls:
         * if edt.getEffectSet()==null: this.getEffectSet() can be anything (null or not)
         * else: this.getEffectSet() can't be null (though can't happen in the first place),
         * and edt.getEffectSet().containsAll(this.getEffectSet())
         */

        /* this.getEffectSet()==null only if edt.getEffectSet()==null
         * (the reverse isn't necessarily true) */
        if ((edt.getEffectSet() != null) && (edt.getEffectSet().getEffects() != null)) {
            if (getEffectSet() == null) {
                return false;
            }
            Set<Effect> thisEffects = recursiveEffectCheck(ctx, getEffectSet().getEffects());
            Set<Effect> edtEffects =  recursiveEffectCheck(ctx, edt.getEffectSet().getEffects());
            if (!edtEffects.containsAll(thisEffects)) {
                return false; // "this" is not a subtype of dt, i.e. not all of its effects are covered by edt's effectSet
            } // effect E = S ("this") <: effect E = S' (edt) if S <= S' (both are concrete)
        }

        /* if edt.getEffectSet()==null (i.e. undefined in the type, or no method annotations),
         * anything (defined in the module def, or the effects of the method calls) is a subtype */
        // i.e. effect E = {} (concrete "this") <: effect E (abstract dt which is undefined)
        return true;
    }

    /** Decompose set of effects to lowest-level ones in scope (obeys any type ascription) **/
    public Set<Effect> recursiveEffectCheck(TypeContext ctx, Set<Effect> effects) {
        if (effects == null) {
            return null;
        }

        Set<Effect> allEffects =  new HashSet<Effect>(); // collects lower-level effects from effectSets of arg "effects"
        Set<Effect> moreEffects = null; // get the effectSet belonging to an effect in arg "effects"
        for (Effect e : effects) {
            EffectSet s = e.effectCheck(ctx);
            if (s != null) {
                View view = View.from(e.getPath(), ctx);
                moreEffects = s.adapt(view).getEffects();
                allEffects.addAll(moreEffects);
            } else { // e was the lowest-level in scope (hidden by type ascription)
                allEffects.add(e);
            }
        }

        // if it is null, then everything in "effects" are of the lowest-level in scope
        if (moreEffects != null) {
            allEffects = recursiveEffectCheck(ctx, allEffects);
        }
        return allEffects;
    }

    @Override
    public void checkWellFormed(TypeContext ctx) {
        // TODO Auto-generated method stub
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        EffectDeclType other = (EffectDeclType) obj;
        if (getName() == null) {
            if (other.getName() != null) {
                return false;
            }
        } else if (!getName().equals(other.getName())) {
            return false;
        }
        if (getEffectSet() == null) {
            if (other.getEffectSet() != null) {
                return false;
            }
        } else if (!getEffectSet().equals(other.getEffectSet())) { //||
            //            !getPath().equals(other.getPath())) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return effectSet.hashCode();
    }

    @Override
    public void doPrettyPrint(Appendable dest, String indent) throws IOException {
        dest.append(indent).append("effect ").append(getName()).append(" = ");
        if (effectSet != null) {
            dest.append(effectSet.toString());
        }
        dest.append('\n');
    }

    @Override
    public DeclType adapt(View v) {
        // TODO: the returned EffectDeclType should have, as its effect set, the set
        // of results from calling adapt(v) on each Effect in this.EffectSet

        //        return new EffectDeclType(getName(), this.getRawResultType().adapt(v));
        EffectSet lb = lowerBound;
        EffectSet up =  upperBound;
        return new EffectDeclType(getName(), effectSet == null ? null : getEffectSet().adapt(v), getLocation(), lb, up);
    }

    @Override
    public DeclType doAvoid(String varName, TypeContext ctx, int count) {
        // TODO: similar to NominalType.doAvoid()
        if (getEffectSet() == null) {
            return this;
        }
        return new EffectDeclType(getName(), getEffectSet().doAvoid(varName, ctx, count), getLocation());
    }

    @Override
    public boolean isTypeOrEffectDecl() {
        return true;
    }

    @Override
    public boolean isEffectAnnotated(TypeContext ctx) {
        return true;
    }

    @Override
    public boolean isEffectUnannotated(TypeContext ctx) {
        return false;
    }
}