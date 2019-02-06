package wyvern.target.corewyvernIL.decl;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import wyvern.stdlib.support.backend.BytecodeOuterClass;
import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.decltype.ConcreteTypeMember;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.expression.IExpr;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.type.Type;
import wyvern.tools.errors.FileLocation;

public class TypeDeclaration extends NamedDeclaration {

    private IExpr metadata;
    private Type sourceType;

    public TypeDeclaration(String typeName, Type sourceType, FileLocation loc) {
        this(typeName, sourceType, null, loc);
    }
    public TypeDeclaration(String typeName, Type sourceType, IExpr metadata, FileLocation loc) {
        super(typeName, loc);
        this.sourceType = sourceType;
        this.metadata = metadata;
    }
    
    public IExpr getMeta() {
        return metadata;
    }

    public Type getSourceType() {
        return sourceType;
    }

    @Override
    public <S, T> T acceptVisitor(ASTVisitor<S, T> emitILVisitor, S state) {
        return emitILVisitor.visit(state, this);
    }

    @Override
    public DeclType typeCheck(TypeContext ctx, TypeContext thisCtx) {
        if (metadata != null) {
            metadata.typeCheck(thisCtx, null);
        }
        sourceType.checkWellFormed(thisCtx);
        DeclType declType = getDeclType();
        return declType;
    }

    @Override
    public void doPrettyPrint(Appendable dest, String indent) throws IOException {
        dest.append(indent).append("type ").append(getName()).append(" = ");
        sourceType.doPrettyPrint(dest, indent);
        dest.append('\n');
    }

    /*@Override
    public String toString() {
        return "type declaration " + super.getName() + " = " + sourceType.toString();
    }*/

    @Override
    public BytecodeOuterClass.Declaration emitBytecode() {
        BytecodeOuterClass.Declaration.TypeDeclaration.Builder td = BytecodeOuterClass.Declaration.TypeDeclaration.newBuilder()
                .setName(getName())
                .setTypeDesc(getSourceType().emitBytecodeTypeDesc());

        return BytecodeOuterClass.Declaration.newBuilder().setTypeDeclaration(td).build();
    }

    @Override
    public Set<String> getFreeVariables() {
        return new HashSet<>();
    }

    /** LIMITATION: only works if sourceType is a ValueType.
     * in the future maybe we'll extract the ValueType from the source type somehow.
     */
    @Override
    public DeclType getDeclType() {
        return new ConcreteTypeMember(getName(), sourceType, this.metadata);
    }

    @Override
    public boolean isTypeOrEffectDecl() {
        return true;
    }
}
