package wyvern.target.corewyvernIL.expression;

import java.util.Arrays;
import java.util.Set;

import wyvern.stdlib.support.backend.BytecodeOuterClass;
import wyvern.target.corewyvernIL.ASTNode;
import wyvern.target.corewyvernIL.IASTNode;
import wyvern.target.corewyvernIL.support.EvalContext;
import wyvern.target.corewyvernIL.support.ILFactory;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.FileLocation;

public abstract class Expression extends ASTNode implements IASTNode, IExpr {

    private ValueType exprType;
//    @Override
//    public abstract ValueType typeCheck(TypeContext ctx);
    public abstract Value interpret(EvalContext ctx);
    public abstract Set<String> getFreeVariables();

    protected Expression(ValueType exprType, FileLocation loc) {
        super(loc);
        this.exprType = exprType;
    }

    protected Expression(ValueType exprType) {
        this.exprType = exprType;
    }

    protected Expression(FileLocation loc) {
        super(loc);
        // if this constructor is used, exprType must be set later!
    }

    protected Expression() {
        // if this constructor is used, exprType must be set later!
    }

    public ValueType getType() {
        return exprType;

    }

    public BytecodeOuterClass.Expression emitBytecode() {
        System.out.println("emitBytecode not implemented for " + this.getClass().getName());
        throw new java.lang.UnsupportedOperationException("Not yet implemented");
    }

    protected void setExprType(ValueType exprType) {
        this.exprType = exprType;
    }

    public MethodCall call(String name, IExpr... args) {
        return ILFactory.instance().call(this, name, Arrays.asList(args));
    }

}
