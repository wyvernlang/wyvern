package wyvern.target.corewyvernIL.decltype;

import java.io.IOException;

import wyvern.stdlib.support.backend.BytecodeOuterClass;
import wyvern.target.corewyvernIL.IASTNode;
import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.support.FailureReason;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.support.View;


public class AbstractTypeMember extends DeclType implements IASTNode {
    private boolean isResource;
    // TODO: add metadata

    public AbstractTypeMember(String name) {
        this(name, false);
    }

    public AbstractTypeMember(String name, boolean isResource) {
        super(name);
        this.isResource = isResource;
    }

    @Override
    public <S, T> T acceptVisitor(ASTVisitor<S, T> emitILVisitor, S state) {
        return emitILVisitor.visit(state, this);
    }

    @Override
    public boolean isSubtypeOf(DeclType dt, TypeContext ctx, FailureReason reason) {
        return this.getName().equals(dt.getName());
    }

    @Override
    public void doPrettyPrint(Appendable dest, String indent) throws IOException {
        dest.append(indent).append("type ").append(getName()).append('\n');
    }

    @Override
    public BytecodeOuterClass.DeclType emitBytecode() {
        BytecodeOuterClass.DeclType.OpaqueTypeDecl.Builder otd = BytecodeOuterClass.DeclType.OpaqueTypeDecl.newBuilder()
                .setName(getName())
                .setStateful(isResource);
        return BytecodeOuterClass.DeclType.newBuilder().setOpaqueTypeDecl(otd).build();
    }

    @Override
    public DeclType adapt(View v) {
        return this;
    }

    @Override
    public void checkWellFormed(TypeContext ctx) {
        // always well-formed!
    }

    @Override
    public DeclType doAvoid(String varName, TypeContext ctx, int count) {
        return this;
    }

    public boolean isResource() {
        return isResource;
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
        return true;
    }
}
