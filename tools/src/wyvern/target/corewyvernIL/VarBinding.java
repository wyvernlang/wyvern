package wyvern.target.corewyvernIL;

import static wyvern.tools.errors.ToolError.reportError;

import java.io.IOException;
import java.util.Set;

import wyvern.target.corewyvernIL.effects.EffectAccumulator;
import wyvern.target.corewyvernIL.expression.IExpr;
import wyvern.target.corewyvernIL.expression.Value;
import wyvern.target.corewyvernIL.support.EvalContext;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.HasLocation;

public class VarBinding implements HasLocation {
    private String varName;
    private ValueType type;
    private IExpr expr;

    public VarBinding(String varName, ValueType type, IExpr toReplace) {
        this.varName = varName;
        this.type = type;
        this.expr = toReplace;
        if (toReplace == null) {
            throw new RuntimeException();
        }
    }

    public String getVarName() {
        return varName;
    }

    public ValueType getType() {
        return type;
    }

    public IExpr getExpression() {
        return expr;
    }

    public TypeContext typecheck(TypeContext ctx, EffectAccumulator effectAccumulator) {
        ValueType t = expr.typeCheck(ctx, effectAccumulator);
        if (!t.isSubtypeOf(type, ctx)) {
            //t.isSubtypeOf(type, ctx); // for debugging
            reportError(ErrorMessage.NOT_SUBTYPE, this, t.toString(), type.toString());
        }
        final TypeContext extendedCtx = ctx.extend(getVarName(), type);
        return extendedCtx;
    }

    public void doPrettyPrint(Appendable dest, String indent) throws IOException {
        dest.append(indent).append(getVarName()).append(" : ");
        getType().doPrettyPrint(dest, indent);
        dest.append(" = ");
        getExpression().doPrettyPrint(dest, indent);
        dest.append('\n');
    }

    public EvalContext interpret(EvalContext ctx) {
        Value v = expr.interpret(ctx);
        return ctx.extend(getVarName(), v);
    }

    @Override
    public FileLocation getLocation() {
        return expr.getLocation();
    }

    public void modFreeVars(Set<String> freeVars) {
        freeVars.remove(varName);
        freeVars.addAll(expr.getFreeVariables());
    }
}
