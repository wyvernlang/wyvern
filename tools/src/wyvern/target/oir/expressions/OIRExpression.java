package wyvern.target.oir.expressions;

import wyvern.target.oir.OIRAST;
import wyvern.target.oir.OIREnvironment;
import wyvern.target.oir.declarations.OIRType;

public abstract class OIRExpression extends OIRAST {
    private OIRType exprType;

    public abstract OIRType typeCheck(OIREnvironment oirEnv);

    public OIRType getExprType() {
        if (exprType == null) {
            exprType = typeCheck(OIREnvironment.getRootEnvironment());
        }
        return exprType;
    }

    public void setExprType(OIRType exprType) {
        this.exprType = exprType;
    }
}
