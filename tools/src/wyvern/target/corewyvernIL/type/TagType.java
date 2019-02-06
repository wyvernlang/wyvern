package wyvern.target.corewyvernIL.type;

import java.util.List;

import wyvern.target.corewyvernIL.decltype.ConcreteTypeMember;
import wyvern.target.corewyvernIL.support.FailureReason;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.support.View;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.ToolError;

public abstract class TagType extends Type {
    private final ValueType valueType;
    private final NominalType parentType;
    private final NominalType selfType;

    public TagType(NominalType parentType, ValueType valueType, NominalType selfType, FileLocation location) {
        super(location);
        this.valueType = valueType;
        this.parentType = parentType;
        this.selfType = selfType;
    }

    public NominalType getParentType(View v) {
        return parentType != null ? (NominalType) parentType.adapt(v) : null;
    }

    public NominalType getSelfType() {
        return selfType;
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
            StructuralType structuralType = parentType.getStructuralType(ctx, null);
            if (structuralType == null) {
                ToolError.reportError(ErrorMessage.NOT_SUBTYPE,
                        this,
                        "Tagged type " + valueType.desugar(ctx),
                        "abstract type " + parentType.desugar(ctx),
                        "");
            }
            if (!valueType.isSubtypeOf(structuralType, ctx, reason)) {
                ToolError.reportError(ErrorMessage.NOT_SUBTYPE,
                        this,
                        "Tagged type " + valueType.desugar(ctx),
                        "extended type " + parentType.desugar(ctx),
                        reason.getReason());
            }
            
            // check any comprises clause in the parent
            ConcreteTypeMember dtm = (ConcreteTypeMember) parentType.getSourceDeclType(ctx);
            if (dtm.getSourceType() instanceof DataType) {
                List<NominalType> cases = ((DataType) dtm.getSourceType()).getCases();
                
                if (!cases.stream().anyMatch(e -> e.nominallyEquals(this.selfType, ctx))) {
                    ToolError.reportError(ErrorMessage.COMPRISES_EXCLUDES_TAG,
                            this,
                            selfType.desugar(ctx),
                            parentType.desugar(ctx));
                }
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
