package wyvern.target.corewyvernIL;

import static wyvern.tools.errors.ToolError.reportError;

import java.io.IOException;
import java.util.Set;

import wyvern.stdlib.support.backend.BytecodeOuterClass;
import wyvern.target.corewyvernIL.effects.EffectAccumulator;
import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.expression.IExpr;
import wyvern.target.corewyvernIL.expression.Value;
import wyvern.target.corewyvernIL.support.EvalContext;
import wyvern.target.corewyvernIL.support.FailureReason;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.HasLocation;

public class VarBinding implements HasLocation {
    private String varName;
    private BindingSite site;
    private ValueType type;
    private IExpr expr;

    public VarBinding(String varName, ValueType type, IExpr toReplace) {
        this(new BindingSite(varName), type, toReplace);
    }

    public VarBinding(BindingSite site, ValueType type, IExpr toReplace) {
        /*if ("ASTIDENT$1".equals(site.getName())) {
            System.out.flush();
        }*/
        this.varName = site.getName();
        this.site = site;
        this.type = type;
        this.expr = toReplace;
        if (toReplace == null) {
            throw new RuntimeException();
        }
    }

    public BytecodeOuterClass.Declaration emitBytecode() {
        BytecodeOuterClass.VariableDeclarationType var = BytecodeOuterClass.VariableDeclarationType.VAR;
        BytecodeOuterClass.Declaration.VariableDeclaration.Builder vd = BytecodeOuterClass.Declaration.VariableDeclaration.newBuilder().setDeclarationType(var)
                .setVariable(getVarName())
                .setType(getType().emitBytecodeType())
                .setInitializer(((Expression) expr).emitBytecode());
        return BytecodeOuterClass.Declaration.newBuilder().setVariableDeclaration(vd).build();
    }

    public String getVarName() {
        return varName;
    }

    public BindingSite getSite() {
        return site;
    }

    public ValueType getType() {
        return type;
    }

    public IExpr getExpression() {
        return expr;
    }

    public TypeContext typecheck(TypeContext ctx, EffectAccumulator effectAccumulator) {
        ValueType t = expr.typeCheck(ctx, effectAccumulator);
        FailureReason r = new FailureReason();
        if (!t.isSubtypeOf(type, ctx, r)) {
            //t.isSubtypeOf(type, ctx); // for debugging
            reportError(ErrorMessage.NOT_SUBTYPE, this, t.desugar(ctx), type.desugar(ctx), r.getReason());
        }
        final TypeContext extendedCtx = ctx.extend(getSite(), type);
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
        return ctx.extend(getSite(), v);
    }

    @Override
    public FileLocation getLocation() {
        return expr.getLocation();
    }

    public void modFreeVars(Set<String> freeVars) {
        freeVars.remove(varName);
        freeVars.addAll(expr.getFreeVariables());
    }
    
    @Override
    public String toString() {
        return getVarName() + " : " + getType() + " = " + getExpression(); 
    }

}
