package wyvern.target.corewyvernIL.support;

import wyvern.target.corewyvernIL.type.ValueType;

public class VarBindingContext extends TypeContext {
    private TypeContext previous;
    private String varName;
    private ValueType type;

    public VarBindingContext(String var, ValueType type, TypeContext typeContext) {
        varName = var;
        this.type = type;
        previous = typeContext;
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
    public ValueType lookupTypeOf(String varName) {
        if (varName.equals(this.varName)) {
            return type;
        } else {
            return previous.lookupTypeOf(varName);
        }
    }

    @Override
    protected TypeContext getNext() {
        return previous;
    }

    @Override
    public String toString() {
        return "VarBindingContext[" + endToString();
    }

    @Override
    protected String endToString() {
        return varName + " : " + type + ", " + getNext().endToString();
    }
}
