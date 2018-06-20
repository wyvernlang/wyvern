package wyvern.target.corewyvernIL.support;

import wyvern.target.corewyvernIL.expression.Value;
import wyvern.target.corewyvernIL.expression.Variable;
import wyvern.target.corewyvernIL.type.ValueType;

public class EmptyValContext extends EvalContext {
    public Value lookupValue(String varName) {
        throw new RuntimeException("Variable " + varName + " not found");
    }

    @Override
    public ValueType lookupTypeOf(Variable v) {
        throw new RuntimeException("Variable " + v.getName() + " not found");
    }

    @Override
    public String endToString() {
        return "]";
    }

    @Override
    protected TypeContext getNext() {
        return null;
    }
}
