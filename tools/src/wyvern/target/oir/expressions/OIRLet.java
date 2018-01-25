package wyvern.target.oir.expressions;

import wyvern.target.oir.ASTVisitor;
import wyvern.target.oir.OIREnvironment;
import wyvern.target.oir.declarations.OIRType;

public class OIRLet extends OIRExpression {
    private String varName;
    private OIRExpression toReplace;
    private OIRExpression inExpr;

    public OIRLet(String varName, OIRExpression toReplace, OIRExpression inExpr) {
        super();
        this.varName = varName;
        this.toReplace = toReplace;
        this.inExpr = inExpr;
    }

    public String getVarName() {
        return varName;
    }

    public void setVarName(String varName) {
        this.varName = varName;
    }

    public OIRExpression getToReplace() {
        return toReplace;
    }

    public void setToReplace(OIRExpression toReplace) {
        this.toReplace = toReplace;
    }

    public OIRExpression getInExpr() {
        return inExpr;
    }

    public void setInExpr(OIRExpression inExpr) {
        this.inExpr = inExpr;
    }

    @Override
    public OIRType typeCheck(OIREnvironment oirEnv) {
        setExprType(inExpr.typeCheck(oirEnv));
        return getExprType();
    }

    @Override
    public <S, T> T acceptVisitor(ASTVisitor<S, T> visitor, S state) {
        return visitor.visit(state, this);
    }
}
