package wyvern.target.corewyvernIL.expression;

import java.io.IOException;
import java.util.Set;

import wyvern.stdlib.support.backend.BytecodeOuterClass;
import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.decltype.DeclTypeWithResult;
import wyvern.target.corewyvernIL.decltype.ValDeclType;
import wyvern.target.corewyvernIL.decltype.VarDeclType;
import wyvern.target.corewyvernIL.effects.EffectAccumulator;
import wyvern.target.corewyvernIL.support.EvalContext;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.support.Util;
import wyvern.target.corewyvernIL.support.View;
import wyvern.target.corewyvernIL.type.StructuralType;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.ToolError;

public class FieldGet extends Expression implements Path {
    private IExpr objectExpr;
    private String fieldName;

    public FieldGet(IExpr objectExpr, String fieldName, FileLocation loc) {
        super(loc);
        this.objectExpr = objectExpr;
        this.fieldName = fieldName;
    }

    @Override
    public void doPrettyPrint(Appendable dest, String indent) throws IOException {
        objectExpr.doPrettyPrint(dest, indent);
        dest.append('.').append(fieldName);
    }

    @Override
    public BytecodeOuterClass.Expression emitBytecode() {
        BytecodeOuterClass.Expression.AccessExpression.Builder ae = BytecodeOuterClass.Expression.AccessExpression.newBuilder()
                .setExpression(((Expression) objectExpr).emitBytecode())
                .setField(fieldName);
        return BytecodeOuterClass.Expression.newBuilder().setAccessExpression(ae).build();
    }

    @Override
    public boolean isPath() {
        return objectExpr.isPath();
    }

    public IExpr getObjectExpr() {
        return objectExpr;
    }

    public String getName() {
        return fieldName;
    }

    @Override
    public ValueType typeCheck(TypeContext ctx, EffectAccumulator effectAccumulator) {
        ValueType vt = objectExpr.typeCheck(ctx, effectAccumulator);
        if (Util.isDynamicType(vt)) {
            return Util.dynType();
        }
        DeclType dt = vt.findDecl(fieldName, ctx);
        if (dt == null) {
            ToolError.reportError(ErrorMessage.NO_SUCH_FIELD, this, fieldName);
        }
        if (!(dt instanceof ValDeclType || dt instanceof VarDeclType)) {
            ToolError.reportError(ErrorMessage.OPERATOR_DOES_NOT_APPLY, this, dt.getName(), objectExpr.toString());
        }
        ValueType resultType = ((DeclTypeWithResult) dt).getResultType(View.from(objectExpr, ctx)); 
        if (!objectExpr.isPath()) {
            // adaptation for the receiver couldn't have worked, so try avoiding "this"
            StructuralType receiverType = vt.getStructuralType(ctx);
            TypeContext calleeCtx = ctx.extend(receiverType.getSelfSite(), vt);
            resultType = resultType.avoid(receiverType.getSelfName(), calleeCtx);
        }
        this.setExprType(resultType);
        return resultType;
    }

    @Override
    public <S, T> T acceptVisitor(ASTVisitor<S, T> emitILVisitor, S state) {
        return emitILVisitor.visit(state, this);
    }

    @Override
    public Value interpret(EvalContext ctx) {
        Value receiver = objectExpr.interpret(ctx);
        if (!(receiver instanceof Invokable)) {
            throw new RuntimeException("expected an object value at field get");
        }
        Invokable ov = (Invokable) receiver;
        return ov.getField(fieldName);
    }

    @Override
    public Path adapt(View v) {
        if (!(objectExpr instanceof Path)) {
            throw new RuntimeException("tried to adapt something that's not a path or type");
        }
        return new FieldGet((Expression) ((Path) objectExpr).adapt(v), fieldName, getLocation());
    }

    @Override
    public Set<String> getFreeVariables() {
        return objectExpr.getFreeVariables();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
        result = prime * result + ((getObjectExpr() == null) ? 0 : getObjectExpr().hashCode());
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
        FieldGet other = (FieldGet) obj;
        if (getName() == null) {
            if (other.getName() != null) {
                return false;
            }
        } else if (!getName().equals(other.getName())) {
            return false;
        }
        if (getObjectExpr() == null) {
            if (other.getObjectExpr() != null) {
                return false;
            }
        } else if (!getObjectExpr().equals(other.getObjectExpr())) {
            return false;
        }
        return true;
    }

    @Override
    public Path adaptVariables(GenContext ctx) {
        if (!(objectExpr instanceof Path)) {
            throw new RuntimeException("invariant violated");
        }
        Path newPath = ((Path) objectExpr).adaptVariables(ctx);
        if (newPath == objectExpr) {
            return this;
        } else {
            return new FieldGet(newPath, fieldName, getLocation());
        }
    }

    @Override
    public boolean hasFreeVariable(String name) {
        if (objectExpr instanceof Path) {
            return ((Path) objectExpr).hasFreeVariable(name);
        } else {
            return objectExpr.getFreeVariables().contains(name);
        }
    }

    @Override
    public void canonicalize(TypeContext ctx) {
        if (objectExpr instanceof Path) {
            ((Path) objectExpr).canonicalize(ctx);
        }
    }
}
