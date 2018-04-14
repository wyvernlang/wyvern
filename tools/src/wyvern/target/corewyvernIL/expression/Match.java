package wyvern.target.corewyvernIL.expression;

import java.util.List;
import java.util.Set;

import wyvern.target.corewyvernIL.Case;
import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.effects.EffectAccumulator;
import wyvern.target.corewyvernIL.support.EvalContext;
import wyvern.target.corewyvernIL.support.FailureReason;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.ToolError;

public class Match extends Expression {

    private Expression matchExpr;
    private Expression elseExpr;
    private List<Case> cases;

    public Match(Expression matchExpr, Expression elseExpr, List<Case> cases) {
        super();
        this.matchExpr = matchExpr;
        this.elseExpr = elseExpr;
        this.cases = cases;
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
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <S, T> T acceptVisitor(ASTVisitor<S, T> emitILVisitor, S state) {
        return emitILVisitor.visit(state, this);
    }

    @Override
    public Value interpret(EvalContext ctx) {
        Value matchValue = matchExpr.interpret(ctx);
        ValueType matchType = matchValue.getType();
        Case matchedCase = null;
        FailureReason reason = new FailureReason();
        for (Case c : cases) {
            ValueType caseMatchedType = c.getPattern();
            if (matchType.isSubtypeOf(caseMatchedType, ctx, reason)) {
                matchedCase = c;
            }
        }
        FailureReason r = new FailureReason();
        if (matchedCase == null && elseExpr == null) {
            ToolError.reportError(ErrorMessage.UNMATCHED_CASE, getLocation(), matchValue.toString());
        }
        if (matchedCase == null) {
            return elseExpr.interpret(ctx);
        } else {
            ctx = ctx.extend(matchedCase.getVarName(), matchValue);
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
