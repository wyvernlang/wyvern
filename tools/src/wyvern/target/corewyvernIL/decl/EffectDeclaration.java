/**
 * IL representation of a defined effect.
 * Its set of effects are checked for validity.
 *
 * @author vzhao
 */
package wyvern.target.corewyvernIL.decl;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.decltype.EffectDeclType;
import wyvern.target.corewyvernIL.effects.EffectSet;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.tools.errors.FileLocation;

public class EffectDeclaration extends NamedDeclaration {
    private EffectSet effectSet;

    public EffectDeclaration(String name, EffectSet effectSet, FileLocation loc) {
        super(name, loc);
        this.effectSet = effectSet;
    }

    @Override
    public <S, T> T acceptVisitor(ASTVisitor<S, T> emitILVisitor, S state) {
        return emitILVisitor.visit(state, this);
    }

    public EffectSet getEffectSet() {
        return effectSet;
    }

    @Override
    public DeclType getDeclType() {
        return new EffectDeclType(getName(), getEffectSet(), getLocation());
    }

    @Override
    /** Iterate through all effects in the set and check that they all exist in the context.
     * Errors reported are: VARIABLE_NOT_DECLARED for objects not found and recursive
     * effect definitions, and EFFECT_NOT_FOUND for effects not from the signature or another object
     */
    public DeclType typeCheck(TypeContext ctx, TypeContext thisCtx) {
        if (effectSet != null) {
            effectSet.effectsCheck(thisCtx);
        }
        return getDeclType();
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
    public Set<String> getFreeVariables() {
        Set<String> freeVars = new HashSet<String>();
        if (effectSet != null) {
            effectSet.getEffects().stream().forEach(e -> freeVars.addAll(e.getPath().getFreeVariables()));
        }
        return freeVars;
    }

    @Override
    public boolean isTypeOrEffectDecl() {
        return true;
    }
}