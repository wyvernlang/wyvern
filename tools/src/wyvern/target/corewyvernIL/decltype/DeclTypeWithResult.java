package wyvern.target.corewyvernIL.decltype;

import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.support.View;
import wyvern.target.corewyvernIL.type.DataType;
import wyvern.target.corewyvernIL.type.ExtensibleTagType;
import wyvern.target.corewyvernIL.type.Type;
import wyvern.target.corewyvernIL.type.ValueType;

public abstract class DeclTypeWithResult extends DeclType {
    private Type rawType;

    DeclTypeWithResult(String name, Type sourceType) {
        super(name);
        this.rawType = sourceType;
    }

    public Type getSourceType() {
        return rawType;
    }

    public ValueType getRawResultType() {
        //TODO: this is a hack, fix it
        if (rawType instanceof DataType || rawType instanceof ExtensibleTagType) {
            return rawType.getValueType();
        } else {
            return ((ValueType) rawType);
        }
    }

    public ValueType getResultType(View v) {
        ValueType t = getRawResultType(); 
        return t.adapt(v);
    }


    @Override
    public void checkWellFormed(TypeContext ctx) {
        rawType.checkWellFormed(ctx);
    }

    @Override
    public boolean isEffectAnnotated(TypeContext ctx) {
        return this.getRawResultType().isEffectAnnotated(ctx);
    }

    @Override
    public boolean isEffectUnannotated(TypeContext ctx) {
        return this.getRawResultType().isEffectUnannotated(ctx);
    }
}
