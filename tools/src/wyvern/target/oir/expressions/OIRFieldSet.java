package wyvern.target.oir.expressions;

import wyvern.target.oir.ASTVisitor;
import wyvern.target.oir.OIREnvironment;
import wyvern.target.oir.declarations.OIRClassDeclaration;
import wyvern.target.oir.declarations.OIRType;

public class OIRFieldSet extends OIRExpression {
    private OIRExpression objectExpr;
    private String fieldName;
    private OIRExpression exprToAssign;
    @Override
    public OIRType typeCheck(OIREnvironment oirEnv) {
        OIRType objectType;

        objectType = objectExpr.typeCheck(oirEnv);
        if (!(objectType instanceof OIRClassDeclaration)) {
            /*TODO: Error object is not class */
            return null;
        }

        setExprType(((OIRClassDeclaration) objectType).getTypeForMember(fieldName));
        return getExprType();
    }
    public OIRFieldSet(OIRExpression objectExpr, String fieldName,
            OIRExpression exprToAssign) {
        super();
        this.objectExpr = objectExpr;
        this.fieldName = fieldName;
        this.exprToAssign = exprToAssign;
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
    public OIRExpression getExprToAssign() {
        return exprToAssign;
    }
    public void setExprToAssign(OIRExpression exprToAssign) {
        this.exprToAssign = exprToAssign;
    }
    @Override
    public <S, T> T acceptVisitor(ASTVisitor<S, T> visitor, S state) {
        return visitor.visit(state, this);
    }
}
