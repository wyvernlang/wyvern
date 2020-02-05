/** @author vzhao */

package wyvern.target.corewyvernIL.decltype;

import java.io.IOException;

import wyvern.stdlib.support.backend.BytecodeOuterClass;
import wyvern.target.corewyvernIL.IASTNode;
import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.effects.EffectSet;
import wyvern.target.corewyvernIL.support.FailureReason;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.support.View;
import wyvern.tools.errors.FileLocation;

/* TODO: adapt(), doAvoid() */
public class EffectDeclType extends DeclType implements IASTNode {
    private EffectSet effectSet;

    private EffectSet supereffect;
    private EffectSet subeffect;

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

    /**
     * Effect Declaration type that declares supereffect or subeffect
     * @param bound Supereffect or Subeffect
     * @param isSupereffect Determine the type of bound
     */
    public EffectDeclType(String name, EffectSet bound, boolean isSupereffect, FileLocation loc) {
        super(name);
        this.effectSet = null;
        if (isSupereffect) {
            this.supereffect = bound;
        } else {
            this.subeffect = bound;
        }
    }

    /**
     * Effect Declaration type that declares supereffect or subeffect
     * @param bound Supereffect or Subeffect
     * @param isSupereffect Determine the type of bound
     */
    public EffectDeclType(String name, EffectSet bound,
                          boolean isSupereffect, EffectSet upperbound, EffectSet lowerbound) {
        super(name);
        this.effectSet = null;
        if (isSupereffect) {
            this.supereffect = bound;
        } else {
            this.subeffect = bound;
        }
        this.upperBound = upperbound;
        this.lowerBound = lowerbound;
    }

    private EffectSet lowerBound;
    private EffectSet upperBound;

    public EffectSet getSupereffect() {
        return supereffect;
    }

    public EffectSet getSubeffect() {
        return subeffect;
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
        if (!(dt instanceof EffectDeclType)) {
            return false;
        }
        EffectDeclType edt = (EffectDeclType) dt;

        if (this.getEffectSet() != null) {
            if (edt.getEffectSet() != null) {
                return this.getEffectSet().isSubeffectOf(edt.getEffectSet(), ctx);
            } else if (edt.getSupereffect() != null) {
                return this.getEffectSet().isSubeffectOf(edt.getSupereffect(), ctx);
            } else if (edt.getSubeffect() != null) {
                return edt.getSubeffect().isSubeffectOf(this.getEffectSet(), ctx);
            } else {
                return true;
            }
        }

        if (this.getSupereffect() != null) {
            if (edt.getEffectSet() != null) {
                return false;
            } else if (edt.getSupereffect() != null) {
                return this.getSupereffect().isSubeffectOf(edt.getSupereffect(), ctx);
            } else {
                return edt.getSubeffect() == null;
            }
        }

        if (this.getSubeffect() != null) {
            if (edt.getEffectSet() != null) {
                return false;
            } else if (edt.getSupereffect() != null) {
                return false;
            } else if (edt.getSubeffect() != null) {
                return edt.getSubeffect().isSubeffectOf(this.getSubeffect(), ctx);
            } else {
                return true;
            }
        }

        return edt.getEffectSet() == null && edt.getSupereffect() == null && edt.getSubeffect() == null;
    }

   @Override
    public void checkWellFormed(TypeContext ctx) {
        if (effectSet != null) {
            effectSet.effectsCheck(ctx);
        }

        if (supereffect != null) {
            supereffect.effectsCheck(ctx);
        }

        if (subeffect != null) {
           subeffect.effectsCheck(ctx);
        }
    }

    @Override
    public void canonicalize(TypeContext ctx) {
        if (effectSet != null) {
            effectSet.canonicalize(ctx);
        }

        if (supereffect != null) {
            supereffect.canonicalize(ctx);
        }

        if (subeffect != null) {
            subeffect.canonicalize(ctx);
        }
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

        if (getSupereffect() == null) {
            if (other.getSupereffect() != null) {
                return false;
            }
        } else if (!getSupereffect().equals(other.getSupereffect())) { //||
            return false;
        }

        if (getSubeffect() == null) {
            if (other.getSubeffect() != null) {
                return false;
            }
        } else if (!getSubeffect().equals(other.getSubeffect())) { //||
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
        dest.append(indent).append("effect ").append(getName());
        if (effectSet != null) {
            dest.append(" = ");
            dest.append(effectSet.toString());
        } else if (supereffect != null) {
            dest.append(" <= ");
            dest.append(supereffect.toString());
        } else if (subeffect != null) {
            dest.append(" >= ");
            dest.append(subeffect.toString());
        }
        dest.append('\n');
    }

    @Override
    public DeclType adapt(View v) {
        // TODO: the returned EffectDeclType should have, as its effect set, the set
        // of results from calling adapt(v) on each Effect in this.EffectSet

        //        return new EffectDeclType(getName(), this.getRawResultType().adapt(v));
        EffectSet lb = lowerBound;
        EffectSet ub =  upperBound;
        if (effectSet != null) {
            return new EffectDeclType(getName(), getEffectSet().adapt(v), getLocation(), lb, ub);
        }

        if (supereffect != null) {
            return new EffectDeclType(getName(), getSupereffect().adapt(v), true, lb, ub);
        }

        if (subeffect != null) {
            return new EffectDeclType(getName(), getSubeffect().adapt(v), false, lb, ub);
        }

        return new EffectDeclType(getName(), null, getLocation(), lb, ub);
    }

    @Override
    public DeclType doAvoid(String varName, TypeContext ctx, int count) {
        // TODO: similar to NominalType.doAvoid()
        if (effectSet != null) {
            EffectSet exactAvoid = getEffectSet().exactAvoid(varName, ctx, count);
            if (exactAvoid != null) {
                return new EffectDeclType(getName(), exactAvoid, getLocation());
            }

            EffectSet increasingAvoid = getEffectSet().increasingAvoid(varName, ctx, count);
            if (increasingAvoid != null) {
                return new EffectDeclType(getName(), increasingAvoid, true, getLocation());
            }

            EffectSet decreasingAvoid = getEffectSet().decreasingAvoid(varName, ctx, count);
            if (decreasingAvoid != null) {
                return new EffectDeclType(getName(), decreasingAvoid, false, getLocation());
            }

            return new EffectDeclType(getName(), null, getLocation());
        }

        if (supereffect != null) {
            EffectSet increasingAvoid = getSupereffect().increasingAvoid(varName, ctx, count);
            if (increasingAvoid != null) {
                return new EffectDeclType(getName(), increasingAvoid, true, getLocation());
            }
            return new EffectDeclType(getName(), null, getLocation());
        }

        if (subeffect != null) {
            EffectSet decreasingAvoid = getSubeffect().decreasingAvoid(varName, ctx, count);
            if (decreasingAvoid != null) {
                return new EffectDeclType(getName(), decreasingAvoid, false, getLocation());
            }
            return new EffectDeclType(getName(), null, getLocation());
        }

        return this;
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