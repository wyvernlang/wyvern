package wyvern.target.corewyvernIL.support;

import wyvern.target.corewyvernIL.expression.Variable;
import wyvern.target.corewyvernIL.type.ValueType;

public class SubtypeAssumption extends TypeContext {
    private TypeContext next;
    private ValueType t1;
    private ValueType t2;

    public SubtypeAssumption(ValueType t1, ValueType t2, TypeContext next) {
        this.next = next;
        this.t1 = t1;
        this.t2 = t2;
    }

    @Override
    public boolean isAssumedSubtype(ValueType at1, ValueType at2) {
        if (at1.equals(t1) && at2.equals(t2)) {
            return true;
        } else {
            return next.isAssumedSubtype(at1, at2);
        }
    }

    @Override
    public ValueType lookupTypeOf(String varName) {
        return next.lookupTypeOf(varName);
    }
    
    @Override
    public ValueType lookupTypeOf(Variable v) {
        return next.lookupTypeOf(v);
    }


    @Override
    protected TypeContext getNext() {
        return next;
    }

    @Override
    public String toString() {
        return "SubtypeAssumption[" + endToString();
    }

    @Override
    protected String endToString() {
        return t1 + " <: " + t2 + ", " + getNext().endToString();
    }
}
