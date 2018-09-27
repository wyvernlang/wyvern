package wyvern.target.corewyvernIL.type;

import java.io.IOException;

import wyvern.stdlib.support.backend.BytecodeOuterClass;
import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.support.FailureReason;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.support.View;

/**
 * Created by Ben Chung on 6/26/2015.
 */
public class DynamicType extends ValueType {

    @Override
    public boolean equals(Object o) {
        return o instanceof DynamicType;
    }

    @Override
    public int hashCode() {
        return DynamicType.class.hashCode();
    }

    @Override
    public boolean isSubtypeOf(ValueType t, TypeContext ctx, FailureReason reason) {
        return true;
    }

    @Override
    public void doPrettyPrint(Appendable dest, String indent, TypeContext ctx) throws IOException {
        dest.append("Dyn");
    }

    @Override
    public BytecodeOuterClass.Type emitBytecodeType() {
        return BytecodeOuterClass.Type.newBuilder().setSimpleType(BytecodeOuterClass.Type.SimpleType.Dyn).build();
    }

    @Override
    public <S, T> T acceptVisitor(ASTVisitor<S, T> emitILVisitor, S state) {
        // TODO Auto-generated method stub
        return null;
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
