package wyvern.target.corewyvernIL.support;

import wyvern.target.corewyvernIL.BindingSite;
import wyvern.target.corewyvernIL.expression.Value;

public class VarEvalContext extends EvalContext {
    private EvalContext previous;
    private BindingSite site;
    private Value v;

    public VarEvalContext(BindingSite site, Value v, EvalContext evalContext) {
        this.site = site;
        this.v = v;
        previous = evalContext;
    }

    @Override
    public boolean isPresent(String varName, boolean isValue) {
        if (isValue && this.site.getName().equals(varName)) {
            return true;
        } else {
            return super.isPresent(varName, isValue);
        }
    }

    @Override
    public Value lookupValue(String varName) {
        if (varName.equals(this.site.getName())) {
            return v;
        } else {
            return previous.lookupValue(varName);
        }
    }

    @Override
    public String endToString() {
        return site.toString()  + " = " + v + ", " + previous.endToString();
    }

    @Override
    protected TypeContext getNext() {
        return previous;
    }
}
