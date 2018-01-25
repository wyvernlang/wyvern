package wyvern.target.oir.expressions;

import wyvern.target.oir.ASTVisitor;
import wyvern.target.oir.OIREnvironment;
import wyvern.target.oir.declarations.OIRClassDeclaration;
import wyvern.target.oir.declarations.OIRType;

public class OIRFieldGet extends OIRExpression {
    private OIRExpression objectExpr;
    public OIRFieldGet(OIRExpression objectExpr, String fieldName) {
        super();
        this.objectExpr = objectExpr;
        this.fieldName = fieldName;
    }
    public OIRExpression getObjectExpr() {
        return objectExpr;
    }
    public void setObjectExpr(OIRExpression objectExpr) {
        this.objectExpr = objectExpr;
    }
    public String getFieldName() {
        return fieldName;
    }
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    private String fieldName;

    @Override
    public OIRType typeCheck(OIREnvironment oirEnv) {
        OIRType type = objectExpr.getExprType();
        if (!(type instanceof OIRClassDeclaration)) {
            /* TODO: Create a new exception here */
            return null;
        }
        setExprType(((OIRClassDeclaration) type).getTypeForMember(fieldName));
        return getExprType();
    }
    @Override
    public <S, T> T acceptVisitor(ASTVisitor<S, T> visitor, S state) {
        return visitor.visit(state, this);
    }
}
