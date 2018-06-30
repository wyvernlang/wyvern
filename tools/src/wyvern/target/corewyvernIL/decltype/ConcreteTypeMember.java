package wyvern.target.corewyvernIL.decltype;

import java.io.IOException;

import wyvern.stdlib.support.backend.BytecodeOuterClass;
import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.expression.IExpr;
import wyvern.target.corewyvernIL.expression.Value;
import wyvern.target.corewyvernIL.support.EvalContext;
import wyvern.target.corewyvernIL.support.FailureReason;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.support.View;
import wyvern.target.corewyvernIL.type.Type;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.ToolError;


public class ConcreteTypeMember extends DeclTypeWithResult implements DefinedTypeMember {

    public ConcreteTypeMember(String name, Type sourceType) {
        this(name, sourceType, null);
    }
    public ConcreteTypeMember(String name, Type sourceType, IExpr metadata) {
        super(name, sourceType);
        this.metadata = metadata;
    }

    private IExpr metadata;

    /*public void setSourceType(ValueType _type)
    {
        sourceType = _type;
    }*/

    @Override
    public void checkWellFormed(TypeContext ctx) {
        /*if (metadata != null) {
            ValueType t = metadata.typeCheck(ctx);
            t.checkWellFormed(ctx);
        }*/
        super.checkWellFormed(ctx);
    }
    @Override
    public Value getMetadataValue() {
        if (metadata != null && !(metadata instanceof Value)) {
            ToolError.reportError(ErrorMessage.CANNOT_USE_METADATA_IN_SAME_FILE, this);
        }
        return (Value) metadata;
    }

    @Override
    public <S, T> T acceptVisitor(ASTVisitor<S, T> emitILVisitor, S state) {
        return emitILVisitor.visit(state, this);
    }

    @Override
    public boolean isSubtypeOf(DeclType dt, TypeContext ctx, FailureReason reason) {
        if (dt instanceof AbstractTypeMember) {
            return true;
        }
        if (!(dt instanceof ConcreteTypeMember)) {
            return false;
        }
        ConcreteTypeMember ctm = (ConcreteTypeMember) dt;
        Type mySourceType = this.getSourceType();
        Type theirSourceType = ctm.getSourceType();
        boolean subtypeRelationship = mySourceType.isTSubtypeOf(theirSourceType, ctx, reason);
        return ctm.getName().equals(getName()) && subtypeRelationship;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
        result = prime * result + ((getSourceType() == null) ? 0 : getSourceType().hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ConcreteTypeMember other = (ConcreteTypeMember) obj;
        if (getName() == null) {
            if (other.getName() != null) {
                return false;
            }
        } else if (!getName().equals(other.getName())) {
            return false;
        }
        if (getSourceType() == null) {
            if (other.getSourceType() != null) {
                return false;
            }
        } else if (!getSourceType().equals(other.getSourceType())) {
            return false;
        }
        return true;
    }

    @Override
    public void doPrettyPrint(Appendable dest, String indent) throws IOException {
        dest.append(indent).append("type ").append(getName()).append(" = ");
        getSourceType().doPrettyPrint(dest, indent);
        dest.append('\n');
    }

    @Override
    public BytecodeOuterClass.DeclType emitBytecode() {

        BytecodeOuterClass.TypeDesc td = getSourceType().emitBytecodeTypeDesc();

        // TODO: serialize metadata
        BytecodeOuterClass.DeclType.DeclTypeDeclaration.Builder dt = BytecodeOuterClass.DeclType.DeclTypeDeclaration.newBuilder().setName(getName())
                .setTypeDesc(td);

        return BytecodeOuterClass.DeclType.newBuilder().setDeclTypeDeclaration(dt).build();
    }

    @Override
    public ConcreteTypeMember adapt(View v) {
        return new ConcreteTypeMember(getName(), this.getSourceType().adapt(v), metadata);
    }

    @Override
    public DeclType interpret(EvalContext ctx) {
        if (metadata == null) {
            return this;
        }
        return new ConcreteTypeMember(getName(), this.getSourceType(), metadata.interpret(ctx));
    }
    @Override
    public ConcreteTypeMember doAvoid(String varName, TypeContext ctx, int count) {
        Type t = this.getSourceType().doAvoid(varName, ctx, count);
        if (t.equals(this.getSourceType())) {
            return this;
        } else {
            return new ConcreteTypeMember(this.getName(), t, metadata);
        }
    }

    @Override
    public boolean isTypeOrEffectDecl() {
        return true;
    }
}
