package wyvern.target.corewyvernIL.support;

import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.expression.Path;
import wyvern.target.corewyvernIL.type.ValueType;

public class VarGenContext extends GenContext {
    private String var;
    private Expression expr;
    private ValueType type;

    public VarGenContext(String var, Expression expr, ValueType type, GenContext genContext) {
        super(genContext);
        if (var == null) {
            throw new NullPointerException();
        }
        this.var = var;
        this.expr = expr;
        this.type = type;
    }

    @Override
    public boolean isPresent(String varName, boolean isValue) {
        if (isValue && this.var.equals(varName)) {
            return true;
        } else {
            return super.isPresent(varName, isValue);
        }
    }

    @Override
    public String toString() {
        return "GenContext[" + endToString();
    }

    @Override
    public String endToString() {
        return var + " : " + type + " = " + expr + ", " + getNext().endToString();
    }

    @Override
    public ValueType lookupTypeOf(String varName) {
        if (varName.equals(var)) {
            return type;
        } else {
            return getNext().lookupTypeOf(varName);
        }
    }

    @Override
    public Path getContainerForTypeAbbrev(String typeName) {
        return getNext().getContainerForTypeAbbrev(typeName);
    }

    @Override
    public CallableExprGenerator getCallableExprRec(String varName, GenContext origCtx) {
        if (varName.equals(var)) {
            return new DefaultExprGenerator(expr);
        } else {
            return getNext().getCallableExprRec(varName, origCtx);
        }
    }
}
