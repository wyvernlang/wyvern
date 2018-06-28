package wyvern.target.corewyvernIL.support;

import wyvern.target.corewyvernIL.expression.Variable;
import wyvern.target.corewyvernIL.type.ValueType;

public class EmptyTypeContext extends TypeContext {
    public ValueType lookupTypeOf(String varName) {
        throw new RuntimeException("Variable " + varName + " not found");
    }

    @Override
    public ValueType lookupTypeOf(Variable v) {
        throw new RuntimeException("Variable " + v.getName() + " not found");
    }

    @Override
    protected TypeContext getNext() {
        return null;
    }

    @Override
    protected String endToString() {
        return "]";
    }
}
