package wyvern.target.corewyvernIL.type;

import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.support.View;

public abstract class TagType extends Type {
    private final ValueType valueType;
    private final NominalType parentType;

    public TagType(NominalType parentType, ValueType valueType) {
        super();
        this.valueType = valueType;
        this.parentType = parentType;
    }

    public NominalType getParentType(View v) {
        return parentType != null ? (NominalType) parentType.adapt(v) : null;
    }

    @Override
    public ValueType getValueType() {
        return valueType;
    }

    @Override
    public void checkWellFormed(TypeContext ctx) {
        valueType.checkWellFormed(ctx);
        if (parentType != null) {
            parentType.checkWellFormed(ctx);
        }
    }

    @Override
    public abstract TagType doAvoid(String varName, TypeContext ctx, int depth);
    @Override
    public abstract TagType adapt(View v);

    protected NominalType getParentType() {
        return parentType;
    }
}
