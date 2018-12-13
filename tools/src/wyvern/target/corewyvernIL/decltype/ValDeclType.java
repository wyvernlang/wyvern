package wyvern.target.corewyvernIL.decltype;

import java.io.IOException;

import wyvern.stdlib.support.backend.BytecodeOuterClass;
import wyvern.target.corewyvernIL.IASTNode;
import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.support.FailureReason;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.support.View;
import wyvern.target.corewyvernIL.type.ValueType;

public class ValDeclType extends DeclTypeWithResult implements IASTNode {

    public ValDeclType(String field, ValueType type) {
        super(field, type);
    }

    @Override
    public <S, T> T acceptVisitor(ASTVisitor<S, T> emitILVisitor, S state) {
        return emitILVisitor.visit(state, this);
    }
    
    @Override
    public boolean containsResource(TypeContext ctx) {
        return this.getRawResultType().isResource(ctx);
    }

    @Override
    public boolean isSubtypeOf(DeclType dt, TypeContext ctx, FailureReason reason) {
        if (!(dt instanceof ValDeclType)) {
            return false;
        }
        ValDeclType vdt = (ValDeclType) dt;
        return vdt.getName().equals(getName()) && this.getRawResultType().isSubtypeOf(vdt.getRawResultType(), ctx, reason);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
        result = prime * result + ((getRawResultType() == null) ? 0 : getRawResultType().hashCode());
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
        ValDeclType other = (ValDeclType) obj;
        if (getName() == null) {
            if (other.getName() != null) {
                return false;
            }
        } else if (!getName().equals(other.getName())) {
            return false;
        }
        if (getRawResultType() == null) {
            if (other.getRawResultType() != null) {
                return false;
            }
        } else if (!getRawResultType().equals(other.getRawResultType())) {
            return false;
        }
        return true;
    }

    @Override
    public void doPrettyPrint(Appendable dest, String indent) throws IOException {
        dest.append(indent).append("val ").append(getName()).append(" : ");
        getRawResultType().doPrettyPrint(dest, indent);
        dest.append('\n');
    }

    @Override
    public BytecodeOuterClass.DeclType emitBytecode() {
        BytecodeOuterClass.DeclType.VariableDeclType.Builder vdt = BytecodeOuterClass.DeclType.VariableDeclType.newBuilder()
                .setDeclarationType(BytecodeOuterClass.VariableDeclarationType.VAL)
                .setVariable(getName())
                .setType(getRawResultType().emitBytecodeType());
        return BytecodeOuterClass.DeclType.newBuilder().setVariableDecl(vdt).build();
    }

    @Override
    public DeclType adapt(View v) {
        return new ValDeclType(getName(), this.getRawResultType().adapt(v));
    }

    @Override
    public DeclType doAvoid(String varName, TypeContext ctx, int count) {
        ValueType t = this.getRawResultType().doAvoid(varName, ctx, count);
        if (t.equals(this.getRawResultType())) {
            return this;
        } else {
            return new ValDeclType(this.getName(), t);
        }
    }

    @Override
    public boolean isTypeOrEffectDecl() {
        return false;
    }
}
