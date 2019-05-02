package wyvern.tools.typedAST.core.expressions;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.expression.IExpr;
import wyvern.target.corewyvernIL.modules.TypedModuleSpec;
import wyvern.target.corewyvernIL.support.FailureReason;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.type.RefinementType;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.ToolError;
import wyvern.tools.typedAST.abs.AbstractExpressionAST;
import wyvern.tools.typedAST.interfaces.CoreAST;
import wyvern.tools.typedAST.interfaces.ExpressionAST;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.typedastvisitor.TypedASTVisitor;
import wyvern.tools.types.Type;

/**
 * Represents a match statement in Wyvern.
 *
 * @author Troy Shaw
 */
public class Match extends AbstractExpressionAST implements CoreAST {

    private ExpressionAST matchingOver;

    private List<Case> cases;
    private Case defaultCase;

    /** Original list which preserves the order and contents. Needed for checking. */
    private List<Case> originalCaseList;

    private FileLocation location;

    public String toString() {
        return "Match: " + matchingOver + " with " + cases + " cases and default: " + defaultCase;
    }

    public ExpressionAST getMatchingOver() {
        return matchingOver;
    }

    public List<Case> getCases() {
        return cases;
    }

    public Case getDefaultCase() {
        return defaultCase;
    }

    @Override
    public <S, T> T acceptVisitor(TypedASTVisitor<S, T> visitor, S state) {
        return visitor.visit(state, this);
    }

    public Match(TypedAST matchingOver, List<Case> cases, FileLocation location) {
        //clone original list so we have a canonical copy
        this.originalCaseList = new ArrayList<Case>(cases);

        this.matchingOver = (ExpressionAST) matchingOver;
        this.cases = cases;

        //find the default case and remove it from the typed cases
        for (Case c : cases) {
            if (c.isDefault()) {
                defaultCase = c;
                break;
            }
        }

        cases.remove(defaultCase);
        this.location = location;
    }

    /**
     * Internal constructor to save from finding the default case again.
     *
     * @param matchingOver
     * @param cases
     * @param defaultCase
     * @param location
     */
    private Match(TypedAST matchingOver, List<Case> cases, Case defaultCase, FileLocation location) {
        this.matchingOver = (ExpressionAST) matchingOver;
        this.cases = cases;
        this.defaultCase = defaultCase;
        this.location = location;
    }

    @Override
    public FileLocation getLocation() {
        return location;
    }

    @Override
    public Expression generateIL(GenContext ctx, ValueType expectedType, List<TypedModuleSpec> dependencies) {
        // First, translate & typecheck the expression we're matching over
        IExpr matchExpr = matchingOver.generateIL(ctx, null, dependencies);
        ValueType expectedMatchType = matchExpr.typeCheck(ctx, null);

        /** Our version of "type unification", where we use the first non-null type to be the type or super-type of all
         * matched expressions/binders. Note that this is not really bidirectional since the matched expression
         * type is the one we originally use.
         */
        for (Case c : cases) {
            Type t = c.getTaggedTypeMatch();
            ValueType vt = (t == null) ? null : t.getILType(ctx);
            FailureReason reason = new FailureReason();
            if (expectedMatchType == null) {
                expectedMatchType = vt;
            } else if (vt != null) {
                ValueType nominalMatchType = expectedMatchType;
                while (nominalMatchType instanceof RefinementType) {
                    nominalMatchType = ((RefinementType) nominalMatchType).getBase();
                }
                if (!(vt.isSubtypeOf(nominalMatchType, ctx, reason))) {
                    ToolError.reportError(ErrorMessage.UNMATCHABLE_CASE, c.getAST(), vt.desugar(ctx), expectedMatchType.desugar(ctx), reason.getReason());
                }
            }
        }
        ValueType matchType = expectedMatchType;


        // Translate & typecheck each individual case, separately for the default case
        Expression elseExpr = null;
        if (defaultCase != null) {
            ExpressionAST defaultExp = defaultCase.getAST();
            elseExpr = (Expression) defaultExp.generateIL(ctx, expectedType, dependencies);
        }
        List<wyvern.target.corewyvernIL.Case> casesIL = cases.stream()
                                                             .map(c -> c.generateILCase(ctx, matchType, matchExpr, expectedType, dependencies))
                                                             .collect(Collectors.toList());
        ValueType expectedCaseType = expectedType;
        for (wyvern.target.corewyvernIL.Case c : casesIL) {
            GenContext caseCtx = ctx.extend(c.getSite(), c.getAdaptedPattern(c.getPattern(), matchType, matchExpr, ctx));
            ValueType caseType = c.getBody().typeCheck(caseCtx, null);
            FailureReason reason = new FailureReason();
            if (expectedCaseType != null && !caseType.isSubtypeOf(expectedCaseType, caseCtx, reason)) {
                FailureReason reason1 = new FailureReason();
                if (expectedType == null && expectedCaseType.isSubtypeOf(caseType, caseCtx, reason1)) {
                    // the first type we inferred is too narrow.  Broaden it to the current type.
                    expectedCaseType = caseType.avoid(c.getVarName(), caseCtx);
                } else {
                    ToolError.reportError(ErrorMessage.CASE_TYPE_MISMATCH, c, expectedCaseType.desugar(caseCtx), caseType.desugar(caseCtx));
                }
            }
            if (expectedCaseType == null) {
                expectedCaseType = caseType.avoid(c.getVarName(), caseCtx);
            }
        }
        return new wyvern.target.corewyvernIL.expression.Match((Expression) matchExpr, elseExpr, casesIL, expectedCaseType, location);
    }
}
