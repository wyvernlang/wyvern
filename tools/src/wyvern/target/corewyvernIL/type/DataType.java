package wyvern.target.corewyvernIL.type;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import wyvern.stdlib.support.backend.BytecodeOuterClass;
import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.support.FailureReason;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.support.View;
import wyvern.tools.errors.FileLocation;

public class DataType extends TagType {
    @Override
    public void doPrettyPrint(Appendable dest, String indent) throws IOException {
        dest.append("datatype ");
        this.getSelfType().doPrettyPrint(dest, indent);
        dest.append(" extends ");
        NominalType parent = this.getParentType(); 
        if (parent == null) {
            dest.append("Top");
        } else {
            parent.doPrettyPrint(dest, indent);
        }
        dest.append(" comprises ");
        dest.append(cases.toString());
    }

    private List<NominalType> cases;

    public DataType(NominalType parentType, ValueType valueType, NominalType selfType, List<NominalType> cases, FileLocation location) {
        super(parentType, valueType, selfType, location);
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
        return new DataType(newCT, getValueType().adapt(v), getSelfType(), newCases, getLocation());
    }

    @Override
    public BytecodeOuterClass.TypeDesc emitBytecodeTypeDesc() {
        // TODO: everything is extag (which is signified by an empty tag)
        BytecodeOuterClass.TypeDesc.Tag.Builder tag = BytecodeOuterClass.TypeDesc.Tag.newBuilder();

        BytecodeOuterClass.Type type = getValueType().emitBytecodeType();

        BytecodeOuterClass.TypeDesc.Builder typeDesc = BytecodeOuterClass.TypeDesc.newBuilder().setTag(tag)
                .setType(type);

        NominalType parentType = getParentType();
        if (parentType != null) {
            typeDesc.setExtends(parentType.getPath() + "." + parentType.getTypeMember());
        }

        return typeDesc.build();
    }

    @Override
    public TagType doAvoid(String varName, TypeContext ctx, int depth) {
        NominalType newCT = getParentType() != null ? (NominalType) getParentType().doAvoid(varName, ctx, depth) : null;
        List<NominalType> newCases = new LinkedList<NominalType>();
        for (NominalType t : cases) {
            newCases.add((NominalType) t.doAvoid(varName, ctx, depth));
        }
        return new DataType(newCT, getValueType().doAvoid(varName, ctx, depth), getSelfType(), newCases, getLocation());
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
        if (!typesEquiv(this.getParentType(), dt.getParentType(), ctx, reason)) {
            return false;
        }
        /*if (!(Objects.equals(this.getParentType(), dt.getParentType()))) {
            return false;
        }*/
        if (this.getCases().size() != dt.getCases().size()) {
            return false;
        }
        for (int i = 0; i < this.getCases().size(); ++i) {
            NominalType c1 = this.getCases().get(i);
            NominalType c2 = dt.getCases().get(i);
            if (!typesEquiv(c1, c2, ctx, reason)) {
                return false;
            }
        }
        return this.getValueType().isSubtypeOf(dt.getValueType(), ctx, reason);
    }
    
}
