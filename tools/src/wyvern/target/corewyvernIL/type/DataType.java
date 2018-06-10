package wyvern.target.corewyvernIL.type;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.support.FailureReason;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.support.View;

public class DataType extends TagType {
    private List<NominalType> cases;

    public DataType(NominalType parentType, ValueType valueType, List<NominalType> cases) {
        super(parentType, valueType);
        this.cases = cases;
    }

    public List<NominalType> getCases() {
        return cases;
    }

    @Override
    public void checkWellFormed(TypeContext ctx) {
        super.checkWellFormed(ctx);
        for (NominalType t:cases) {
            t.checkWellFormed(ctx);
        }
    }

    public <S, T> T acceptVisitor(ASTVisitor<S, T> emitILVisitor, S state) {
        return emitILVisitor.visit(state, this);
    }

    @Override
    public TagType adapt(View v) {
        NominalType newCT = (NominalType) getParentType(v);
        List<NominalType> newCases = new LinkedList<NominalType>();
        for (NominalType t : cases) {
            newCases.add((NominalType) t.adapt(v));
        }
        return new DataType(newCT, getValueType().adapt(v), newCases);
    }

    @Override
    public TagType doAvoid(String varName, TypeContext ctx, int depth) {
        NominalType newCT = getParentType() != null ? (NominalType) getParentType().doAvoid(varName, ctx, depth) : null;
        List<NominalType> newCases = new LinkedList<NominalType>();
        for (NominalType t : cases) {
            newCases.add((NominalType) t.doAvoid(varName, ctx, depth));
        }
        return new DataType(newCT, getValueType().doAvoid(varName, ctx, depth), newCases);
    }

    @Override
    public boolean isTagged(TypeContext ctx) {
        return true;
    }

    @Override
    public boolean isTSubtypeOf(Type sourceType, TypeContext ctx, FailureReason reason) {
        if (!(sourceType instanceof DataType)) {
            return false;
        }
        DataType dt = (DataType) sourceType;
        if (!(Objects.equals(this.getParentType(), dt.getParentType()))) {
            return false;
        }
        if (!this.getCases().equals(dt.getCases())) {
            return false;
        }
        return this.getValueType().isSubtypeOf(dt.getValueType(), ctx, reason);
    }
}
