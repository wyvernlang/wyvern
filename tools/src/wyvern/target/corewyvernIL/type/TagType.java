package wyvern.target.corewyvernIL.type;

import wyvern.target.corewyvernIL.support.FailureReason;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.support.View;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.ToolError;

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
            FailureReason reason = new FailureReason();
            if (!valueType.isSubtypeOf(parentType.getStructuralType(ctx), ctx, reason)) {
                ToolError.reportError(ErrorMessage.NOT_SUBTYPE,
                        this,
                        "Tagged type " + valueType.desugar(ctx),
                        "extended type " + parentType.desugar(ctx),
                        reason.getReason());

            }
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
