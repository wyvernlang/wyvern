package wyvern.target.corewyvernIL.expression;

import java.util.Set;

import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.effects.EffectAccumulator;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.FileLocation;

public abstract class SuspendedTailCall extends Expression implements Value {

    protected SuspendedTailCall(ValueType exprType, FileLocation loc) {
        super(exprType, loc);
    }

    @Override
    public ValueType getType() {
        return this.getType();
    }

    @Override
    public <S, T> T acceptVisitor(ASTVisitor<S, T> visitor, S state) {
        throw new RuntimeException("should not try to visit a suspended tail call");
    }

    @Override
    public ValueType typeCheck(TypeContext ctx, EffectAccumulator effectAccumulator) {
        throw new RuntimeException("should not try to typecheck a suspended tail call");
    }

    @Override
    public Set<String> getFreeVariables() {
        throw new RuntimeException("should not try to get the free variables of a suspended tail call");
    }

}
