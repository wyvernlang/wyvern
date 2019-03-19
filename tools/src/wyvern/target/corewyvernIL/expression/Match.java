package wyvern.target.corewyvernIL.expression;

import java.util.List;
import java.util.Set;

import wyvern.stdlib.support.backend.BytecodeOuterClass;
import wyvern.target.corewyvernIL.Case;
import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.effects.EffectAccumulator;
import wyvern.target.corewyvernIL.support.EvalContext;
import wyvern.target.corewyvernIL.support.FailureReason;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.type.NominalType;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.ToolError;
import wyvern.tools.interop.FFI;

public class Match extends Expression {

    private Expression matchExpr;
    private Expression elseExpr;
    private List<Case> cases;

    public Match(Expression matchExpr, Expression elseExpr, List<Case> cases, ValueType caseType, FileLocation location) {
        super(caseType, location);
        this.matchExpr = matchExpr;
        this.elseExpr = elseExpr;
        this.cases = cases;
    }

    public Match(Expression matchExpr, Expression elseExpr, List<Case> cases, FileLocation location) {
        this(matchExpr, elseExpr, cases, null, location);
    }

    public Expression getMatchExpr() {
        return matchExpr;
    }

    public Expression getElseExpr() {
        return elseExpr;
    }

    public List<Case> getCases() {
        return cases;
    }

    @Override
    public ValueType typeCheck(TypeContext env, EffectAccumulator effectAccumulator) {
        // typecheck the match expression
        ValueType matchType = matchExpr.typeCheck(env, effectAccumulator);
        if (!matchType.isTagged(env)) {
            ToolError.reportError(ErrorMessage.TYPE_NOT_TAGGED, matchExpr, matchType.desugar(env));
        }

        if (getType() == null) {
            Case c = cases.get(0);
            TypeContext caseCtx = env.extend(c.getSite(), c.getPattern());
            ValueType type = c.getBody().typeCheck(caseCtx, effectAccumulator);
            this.setExprType(type);
        }

        // typecheck the default case
        if (elseExpr != null) {
            ValueType elseType = elseExpr.typeCheck(env, effectAccumulator);
            FailureReason reason = new FailureReason();
            if (!elseType.isSubtypeOf(getType(), env, reason)) {
                ToolError.reportError(ErrorMessage.CASE_TYPE_MISMATCH, elseExpr, elseType.desugar(env), this.getType().desugar(env));
            }
        }

        // typecheck the other cases
        for (Case c : cases) {
            FailureReason reason = new FailureReason();
            if (!c.getAdaptedPattern(c.getPattern(), matchType, matchExpr, env).isSubtypeOf(matchType, env, reason)) {
                ToolError.reportError(ErrorMessage.UNMATCHABLE_CASE, c, c.getPattern().desugar(env), matchType.desugar(env), reason.getReason());
            }

            TypeContext caseCtx = env.extend(c.getSite(), c.getAdaptedPattern(c.getPattern(), matchType, matchExpr, env));
            ValueType caseType = c.getBody().typeCheck(caseCtx, effectAccumulator);
            reason = new FailureReason();
            if (!caseType.isSubtypeOf(getType(), caseCtx, reason)) {
                ToolError.reportError(ErrorMessage.CASE_TYPE_MISMATCH, elseExpr, caseType.desugar(env), this.getType().desugar(env));
            }
        }
        return getType();
    }

    @Override
    public <S, T> T acceptVisitor(ASTVisitor<S, T> emitILVisitor, S state) {
        return emitILVisitor.visit(state, this);
    }

    @Override
    public BytecodeOuterClass.Expression emitBytecode() {
        BytecodeOuterClass.Expression.MatchExpression.Builder matchExpression = BytecodeOuterClass.Expression.MatchExpression.newBuilder()
                .setExpression(matchExpr.emitBytecode());

        if (elseExpr != null) {
            matchExpression.setElse(elseExpr.emitBytecode());
        }
        for (Case c: cases) {
            NominalType pattern = c.getPattern();
            String path = pattern.getPath() + "." + pattern.getTypeMember();
            BytecodeOuterClass.Expression.MatchExpression.MatchArm.Builder arm = BytecodeOuterClass.Expression.MatchExpression.MatchArm.newBuilder()
                    .setVariable(c.getVarName())
                    .setPath(path)
                    .setExpression(c.getBody().emitBytecode());

            matchExpression.addArms(arm);
        }
        return BytecodeOuterClass.Expression.newBuilder().setMatchExpression(matchExpression).build();
    }

    @Override
    public Value interpret(EvalContext ctx) {
        Value matchValue = matchExpr.interpret(ctx);
        Tag matchTag;
        if (matchValue instanceof  ObjectValue) {
            matchTag = ((ObjectValue) matchValue).getTag();
        } else if (matchValue instanceof FFI) {
            matchTag = ((FFI) matchValue).getTag(ctx);
        } else {
            throw new UnsupportedOperationException("Attempted to match on value without tag");
        }
        Case matchedCase = null;
        FailureReason reason = new FailureReason();
        for (Case c : cases) {
            ValueType caseMatchedType = c.getPattern();
            if (matchTag.isSubTag(caseMatchedType.getTag(ctx), ctx)) {
                matchedCase = c;
                break;
            }
        }
        if (matchedCase == null && elseExpr == null) {
            ToolError.reportError(ErrorMessage.UNMATCHED_CASE, getLocation(), matchTag.toString());
        }
        if (matchedCase == null) {
            return elseExpr.interpret(ctx);
        } else {
            ctx = ctx.extend(matchedCase.getSite(), matchValue);
            Expression caseBody = matchedCase.getBody();
            return caseBody.interpret(ctx);
        }
    }

    @Override
    public Set<String> getFreeVariables() {
        Set<String> freeVars = matchExpr.getFreeVariables();
        for (Case c : cases) {
            freeVars.addAll(c.getBody().getFreeVariables());
            freeVars.remove(c.getVarName());

            // TODO: fix when Match is implemented
            // Add variable at the root of the path of the NominalType
            // Look up that variable to get the tag to do the tag check.
            // Path p = c.getPattern().getPath();
            // if p returns y.f.x, the variable at the root of the path = y

        }
        if (elseExpr != null) {
            freeVars.addAll(elseExpr.getFreeVariables());
        }
        return freeVars;
    }
}
