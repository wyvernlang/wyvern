package wyvern.target.oir.expressions;

import wyvern.target.oir.ASTVisitor;
import wyvern.target.oir.OIREnvironment;
import wyvern.target.oir.declarations.OIRType;

public class OIRIfThenElse extends OIRExpression {
    private OIRExpression condition;
    private OIRExpression thenExpression;
    private OIRExpression elseExpression;

    public OIRExpression getCondition() {
        return condition;
    }

    public void setCondition(OIRExpression condition) {
        this.condition = condition;
    }

    public OIRExpression getThenExpression() {
        return thenExpression;
    }

    public void setThenExpression(OIRExpression thenExpression) {
        this.thenExpression = thenExpression;
    }

    public OIRExpression getElseExpression() {
        return elseExpression;
    }

    public void setElseExpression(OIRExpression elseExpression) {
        this.elseExpression = elseExpression;
    }

    public OIRIfThenElse(OIRExpression condition, OIRExpression thenExpression,
            OIRExpression elseExpression) {
        super();
        this.condition = condition;
        this.thenExpression = thenExpression;
        this.elseExpression = elseExpression;
    }

    @Override
    public OIRType typeCheck(OIREnvironment oirEnv) {
        OIRType thenType;
        OIRType elseType;

        thenType = thenExpression.typeCheck(oirEnv);
        elseType = elseExpression.typeCheck(oirEnv);

        if (thenType != elseType) {
            /*TODO Error type mismatch */
            return null;
        }

        setExprType(thenType);
        return thenType;
    }

    @Override
    public <S, T> T acceptVisitor(ASTVisitor<S, T> visitor, S state) {
        return visitor.visit(state, this);
    }
}
