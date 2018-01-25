package wyvern.target.corewyvernIL.support;

import wyvern.target.corewyvernIL.expression.Value;

public class VarEvalContext extends EvalContext {
    private EvalContext previous;
    private String varName;
    private Value v;

    public VarEvalContext(String var, Value v, EvalContext evalContext) {
        varName = var;
        this.v = v;
        previous = evalContext;
    }

    @Override
    public boolean isPresent(String varName, boolean isValue) {
        if (isValue && this.varName.equals(varName)) {
            return true;
        } else {
            return super.isPresent(varName, isValue);
        }
    }

    @Override
    public Value lookupValue(String varName) {
        if (varName.equals(this.varName)) {
            return v;
        } else {
            return previous.lookupValue(varName);
        }
    }

    @Override
    public String endToString() {
        return varName  + " = " + v + ", " + previous.endToString();
    }

    @Override
    protected TypeContext getNext() {
        return previous;
    }
}
