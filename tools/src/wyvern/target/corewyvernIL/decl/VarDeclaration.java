package wyvern.target.corewyvernIL.decl;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import wyvern.stdlib.support.backend.BytecodeOuterClass;
import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.decltype.VarDeclType;
import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.expression.IExpr;
import wyvern.target.corewyvernIL.support.EvalContext;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.FileLocation;

public class VarDeclaration extends DeclarationWithRHS {

    private ValueType type;

    /*@Override
    public String toString() {
        return "VarDeclaration[" + getName() + " : " + type + " = " + getDefinition() + "]";
    }*/

    public VarDeclaration(String name, ValueType type, IExpr value, FileLocation loc) {
        super(name, value, loc);
        if (type == null) {
            throw new RuntimeException();
        }
        this.type = type;
    }

    @Override
    public boolean containsResource(TypeContext ctx) {
        return true;
    }

    @Override
    public void doPrettyPrint(Appendable dest, String indent) throws IOException {
        dest.append(indent).append("var ").append(getName()).append(':');
        type.doPrettyPrint(dest, indent);
        dest.append(" = ");
        getDefinition().doPrettyPrint(dest, indent);
        dest.append('\n');
    }

    @Override
    public BytecodeOuterClass.Declaration emitBytecode() {
        BytecodeOuterClass.Declaration.VariableDeclaration.Builder vd = BytecodeOuterClass.Declaration.VariableDeclaration.newBuilder()
                .setDeclarationType(BytecodeOuterClass.VariableDeclarationType.VAR)
                .setVariable(getName())
                .setType(getType().emitBytecodeType())
                .setInitializer(((Expression) getDefinition()).emitBytecode());
        return BytecodeOuterClass.Declaration.newBuilder().setVariableDeclaration(vd).build();
    }

    @Override
    public ValueType getType() {
        return type;
    }

    @Override
    public <S, T> T acceptVisitor(ASTVisitor<S, T> emitILVisitor, S state) {
        return emitILVisitor.visit(state, this);
    }

    @Override
    public DeclType getDeclType() {
        return new VarDeclType(getName(), type);
    }

    @Override
    public Declaration interpret(EvalContext ctx) {
        Expression newValue = (Expression) getDefinition().interpret(ctx);
        return new VarDeclaration(getName(), type, newValue, getLocation());
    }

    public Set<String> getFreeVariables() {
        Set<String> freeVars = new HashSet<>();
        freeVars.addAll(getDefinition().getFreeVariables());
        return freeVars;
    }

}
