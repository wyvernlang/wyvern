package wyvern.target.corewyvernIL.support;

import wyvern.target.corewyvernIL.BindingSite;
import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.expression.Path;
import wyvern.target.corewyvernIL.type.ValueType;

public class VarGenContext extends GenContext {
    private BindingSite site;
    private String name;
    private Expression expr;
    private ValueType type;

    public VarGenContext(BindingSite varBinding, Expression expr, ValueType type, GenContext genContext) {
        super(genContext);
        if (varBinding == null) {
            throw new NullPointerException();
        }
        this.site = varBinding;
        this.name = site.getName();
        this.expr = expr;
        this.type = type;
    }

    public VarGenContext(String varName, Expression expr, ValueType type, GenContext genContext) {
        super(genContext);
        this.name = varName;
        this.site = null;
        this.expr = expr;
        this.type = type;
    }

    @Override
    public boolean isPresent(String varName, boolean isValue) {
        if (isValue && this.name.equals(varName)) {
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
        return name.toString() + " : " + type + " = " + expr + ", " + getNext().endToString();
    }

    @Override
    public ValueType lookupTypeOf(String varName) {
        if (varName.equals(name)) {
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
        if (varName.equals(name)) {
            return new DefaultExprGenerator(expr);
        } else {
            return getNext().getCallableExprRec(varName, origCtx);
        }
    }
}
