package wyvern.target.corewyvernIL.decltype;

import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.support.View;
import wyvern.target.corewyvernIL.type.DataType;
import wyvern.target.corewyvernIL.type.Type;
import wyvern.target.corewyvernIL.type.ValueType;

public abstract class DeclTypeWithResult extends DeclType {
    private Type rawType;

    DeclTypeWithResult(String name, Type sourceType) {
        super(name);
        this.rawType = sourceType;
    }

    public ValueType getRawResultType() {
        //TODO: this is a hack, fix it
        if (rawType instanceof DataType) {
            DataType dt = (DataType) rawType;
            return dt.getValueType();
        } else {
            return ((ValueType) rawType);
        }
    }

    public ValueType getResultType(View v) {
        return getRawResultType().adapt(v);
    }


    @Override
    public void checkWellFormed(TypeContext ctx) {
        rawType.checkWellFormed(ctx);
    }

}
