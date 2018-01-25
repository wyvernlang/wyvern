package wyvern.target.corewyvernIL.support;

import wyvern.target.corewyvernIL.type.NominalType;
import wyvern.target.corewyvernIL.type.ValueType;

public class SubtypeAssumption extends TypeContext {
    private TypeContext next;
    private NominalType t1;
    private ValueType t2;

    public SubtypeAssumption(NominalType t1, ValueType t2, TypeContext next) {
        this.next = next;
        this.t1 = t1;
        this.t2 = t2;
    }

    @Override
    public boolean isAssumedSubtype(NominalType at1, ValueType at2) {
        if (at1.equals(t1) && at2.equals(t2)) {
            return true;
        } else {
            return isAssumedSubtype(t1, t2);
        }
    }

    @Override
    public ValueType lookupTypeOf(String varName) {
        return next.lookupTypeOf(varName);
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
