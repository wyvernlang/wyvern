package wyvern.target.corewyvernIL.decl;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import wyvern.stdlib.support.backend.BytecodeOuterClass;
import wyvern.target.corewyvernIL.FormalArg;
import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.decltype.DefDeclType;
import wyvern.target.corewyvernIL.effects.Effect;
import wyvern.target.corewyvernIL.effects.EffectAccumulator;
import wyvern.target.corewyvernIL.effects.EffectSet;
import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.expression.IExpr;
import wyvern.target.corewyvernIL.expression.Variable;
import wyvern.target.corewyvernIL.support.FailureReason;
import wyvern.target.corewyvernIL.support.QuantificationLifter;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.ToolError;

public class DefDeclaration extends NamedDeclaration {
    private final List<FormalArg> formalArgs;
    private final ValueType type;
    private final IExpr body;
    private boolean hasResource = false;
    private final EffectSet effectSet;

    public DefDeclaration(String methodName, List<FormalArg> formalArgs,
            ValueType type, IExpr iExpr, FileLocation loc) {
        this(methodName, formalArgs, type, iExpr, loc, null);
    }

    public DefDeclaration(String methodName, List<FormalArg> formalArgs,
            ValueType type, IExpr iExpr, FileLocation loc, EffectSet effectSet) {
        super(methodName, loc);
        this.formalArgs = formalArgs;
        if (type == null) {
            throw new RuntimeException();
        }
        this.type = type;
        body = iExpr;
        this.effectSet = effectSet;

    }

    @Override
    public boolean containsResource(TypeContext ctx) {
        return hasResource;
    }

    private void setHasResource(boolean hasResource) {
        this.hasResource = hasResource;
    }

    @Override
    public void doPrettyPrint(Appendable dest, String indent) throws IOException {
        dest.append(indent).append("def ").append(getName()).append('(');
        boolean first = true;
        for (final FormalArg arg: formalArgs) {
            if (first) {
                first = false;
            } else {
                dest.append(", ");
            }
            arg.doPrettyPrint(dest, indent);
        }
        final String newIndent = indent + "    ";
        dest.append(") : ");
        if (effectSet != null) {
            dest.append(effectSet.toString());
        }
        type.doPrettyPrint(dest, newIndent);
        dest.append('\n').append(newIndent);
        body.doPrettyPrint(dest, newIndent);
        dest.append('\n');
    }

    public List<FormalArg> getFormalArgs() {
        return formalArgs;
    }

    public ValueType getType() {
        return type;
    }

    public IExpr getBody() {
        return body;
    }

    public EffectSet getEffectSet() {
        return effectSet;
    }

    @Override
    public <S, T> T acceptVisitor(ASTVisitor<S, T> emitILVisitor, S state) {
        return emitILVisitor.visit(state, this);
    }

    @Override
    public DeclType typeCheck(TypeContext ctx, TypeContext thisCtx) {

        TypeContext methodCtx = thisCtx;
        for (final FormalArg arg : formalArgs) {
            methodCtx = methodCtx.extend(arg.getSite(), arg.getType());
        }
        if (!containsResource(methodCtx)) {
            for (final String freeVar : getFreeVariables()) {
                final ValueType t = new Variable(freeVar).typeCheck(methodCtx, null);
                if (t != null && t.isResource(methodCtx)) {
                    setHasResource(true);
                    break;
                }
            }
        }

        // if the method makes no claim about the effects it has, do not check its calls for effects (i.e. null)
        final EffectAccumulator effectAccumulator = effectSet == null ? null : new EffectAccumulator();

        final ValueType bodyType = body.typeCheck(methodCtx, effectAccumulator);

        if (effectSet != null && !QuantificationLifter.isMonomorphized(effectSet)) {
            effectsCheck(methodCtx, effectAccumulator);
        }

        final FailureReason r = new FailureReason();
        if (bodyType != null && !bodyType.isSubtypeOf(getType(), methodCtx, r)) {
            // for debugging
            final ValueType resultType = getType();
            bodyType.isSubtypeOf(resultType, methodCtx, r);
            ToolError.reportError(ErrorMessage.NOT_SUBTYPE,
                    this,
                    "Method body's type " + bodyType.desugar(methodCtx),
                    "declared type " + resultType.desugar(thisCtx),
                    r.getReason());

        }
        return new DefDeclType(getName(), type, formalArgs, effectSet);
    }

    /** check that all effects in annotation exist (assume that those from method calls are valid). */
    private void effectsCheck(TypeContext methodCtx, EffectAccumulator effectAccumulator) {
        // TODO: make uniform, regardless of whether we're in an obj definition or module def

        if (effectSet.getEffects() != null) {
           effectSet.effectsCheck(methodCtx);

            final Set<Effect> actualEffectSet = effectAccumulator.getEffectSet();

            // compare method call effects with annotated ones
            final FailureReason r = new FailureReason();
            if (!(new EffectSet(actualEffectSet)).isSubeffectOf(effectSet, methodCtx)) {
                ToolError.reportError(ErrorMessage.NOT_SUBTYPE, getLocation(),
                        "Effect annotation " + effectSet.toString() + " on method " + getName(),
                        "effects that method produces, which are " + actualEffectSet.toString(),
                        r.getReason());
            }
        }
    }

    @Override
    public BytecodeOuterClass.Declaration emitBytecode() {
        final BytecodeOuterClass.Declaration.MethodDeclaration.Builder md = BytecodeOuterClass.Declaration.MethodDeclaration.newBuilder()
                .setMethodName(getName())
                .setReturnType(getType().emitBytecodeType())
                .setBody(((Expression) body).emitBytecode());

        for (final FormalArg arg : formalArgs) {
            md.addArguments(arg.emitBytecode());
        }
        return BytecodeOuterClass.Declaration.newBuilder().setMethodDeclaration(md).build();
    }

    @Override
    public Set<String> getFreeVariables() {
        // Get all free variables in the body of the method.
        final Set<String> freeVars = body.getFreeVariables();

        // Remove variables that became bound in this method's scope.
        for (final FormalArg farg : formalArgs) {
            freeVars.remove(farg.getName());
        }
        return freeVars;
    }

    @Override
    public DefDeclType getDeclType() {
        return new DefDeclType(getName(), type, formalArgs, getEffectSet());
    }
}
