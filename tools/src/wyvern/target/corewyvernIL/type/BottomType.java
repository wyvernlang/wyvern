package wyvern.target.corewyvernIL.type;

import java.io.IOException;

import wyvern.stdlib.support.backend.BytecodeOuterClass;
import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.support.FailureReason;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.support.View;

/**
 * @author aldrich - Jonathan Aldrich
 */
public class BottomType extends ValueType {

    @Override
    public boolean equals(Object o) {
        return o instanceof BottomType;
    }

    @Override
    public int hashCode() {
        return BottomType.class.hashCode();
    }

    @Override
    public boolean isSubtypeOf(ValueType t, TypeContext ctx, FailureReason reason) {
        return true;
    }

    @Override
    public void doPrettyPrint(Appendable dest, String indent, TypeContext ctx) throws IOException {
        dest.append("Nothing");
    }

    @Override
    public <S, T> T acceptVisitor(ASTVisitor<S, T> emitILVisitor, S state) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public BytecodeOuterClass.Type emitBytecodeType() {
        return BytecodeOuterClass.Type.newBuilder().setSimpleType(BytecodeOuterClass.Type.SimpleType.Nothing).build();
    }

    @Override
    public ValueType adapt(View v) {
        return this;
    }

    @Override
    public void checkWellFormed(TypeContext ctx) {
        // this type is always well-formed!
    }

    @Override
    public ValueType doAvoid(String varName, TypeContext ctx, int count) {
        return this;
    }

    @Override
    public boolean isTagged(TypeContext ctx) {
        return false;
    }

    @Override
    public boolean isEffectAnnotated(TypeContext ctx) {
        return true;
    }

    @Override
    public boolean isEffectUnannotated(TypeContext ctx) {
        return true;
    }
}
