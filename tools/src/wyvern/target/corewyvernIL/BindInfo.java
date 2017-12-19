package wyvern.target.corewyvernIL;

import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.type.ValueType;

public class BindInfo {
    public BindInfo(ValueType type, TypeContext ctx) {
        typ = type;
        c = ctx;
    }
    private TypeContext c;
    private ValueType typ;
    public ValueType type() {
        return typ;
    }
    public TypeContext ctx() {
        return c;
    }
}
