package wyvern.target.corewyvernIL.type;

import java.io.IOException;
import java.util.Objects;

import wyvern.stdlib.support.backend.BytecodeOuterClass;
import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.support.FailureReason;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.support.View;
import wyvern.tools.errors.FileLocation;

public class ExtensibleTagType extends TagType {

    public ExtensibleTagType(NominalType parentType, ValueType valueType, NominalType selfType, FileLocation location) {
        super(parentType, valueType, selfType, location);
    }

    public <S, T> T acceptVisitor(ASTVisitor<S, T> emitILVisitor,
            S state) {
        return emitILVisitor.visit(state, this);
    }

    @Override
    public void doPrettyPrint(Appendable dest, String indent) throws IOException {
        dest.append("tagged type ")
            .append(this.getSelfType().getTypeMember())
            .append(" extends ");
        NominalType parent = this.getParentType(); 
        if (parent == null) {
            dest.append("Top");
        } else {
            parent.doPrettyPrint(dest, indent);
        }
    }

    @Override
    public TagType adapt(View v) {
        return new ExtensibleTagType((NominalType) getParentType(v), getValueType().adapt(v), getSelfType(), getLocation());
    }

    @Override
    public TagType doAvoid(String varName, TypeContext ctx, int depth) {
        final NominalType newPT = getParentType() != null ? (NominalType) getParentType().doAvoid(varName, ctx, depth) : null;
        return new ExtensibleTagType(newPT, getValueType().doAvoid(varName, ctx, depth), getSelfType(), getLocation());
    }

    @Override
    public BytecodeOuterClass.TypeDesc emitBytecodeTypeDesc() {
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
    public boolean isTagged(TypeContext ctx) {
        return true;
    }

    @Override
    public boolean isTSubtypeOf(Type sourceType, TypeContext ctx, FailureReason reason) {
        if (!(sourceType instanceof ExtensibleTagType)) {
            return false;
        }
        ExtensibleTagType extensibleTagType = (ExtensibleTagType) sourceType;
        if (!(Objects.equals(this.getParentType(), extensibleTagType.getParentType()))) {
            // not obviously equal; use a more complex test to see if they are unequal after following links
            if (!this.getParentType().nominallyEquals(extensibleTagType.getParentType(), ctx)) {
                return false;
            }
        }
        return this.getValueType().isSubtypeOf(extensibleTagType.getValueType(), ctx, reason);
    }
}
