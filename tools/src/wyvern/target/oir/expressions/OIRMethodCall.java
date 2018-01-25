package wyvern.target.oir.expressions;

import java.util.List;

import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.target.oir.ASTVisitor;
import wyvern.target.oir.OIREnvironment;
import wyvern.target.oir.declarations.OIRClassDeclaration;
import wyvern.target.oir.declarations.OIRInterface;
import wyvern.target.oir.declarations.OIRType;

public class OIRMethodCall extends OIRExpression {
    private OIRExpression objectExpr;
    private String methodName;
    private List<OIRExpression> args;
    private ValueType objectType;
    @Override
    public OIRType typeCheck(OIREnvironment oirEnv) {
        OIRType type = objectExpr.typeCheck(oirEnv);
        if (type instanceof OIRClassDeclaration) {
            OIRType oirType;
            oirType = ((OIRClassDeclaration) type).getTypeForMember(methodName);
            if (oirType == null) {
                /* TODO: Error */
                throw new RuntimeException("Error - no type for member " + methodName + " found.");
            }
            setExprType(oirType);
            return getExprType();
        } else if (type instanceof OIRInterface) {
            setExprType(((OIRInterface) type).getTypeForMember(methodName));
            return getExprType();
        }
        /* TODO: Throw type is not class */
        return null;
    }
    public OIRMethodCall(OIRExpression objectExpr, ValueType objectType,
            String methodName, List<OIRExpression> args) {
        super();
        this.objectExpr = objectExpr;
        this.objectType = objectType;
        this.methodName = methodName;
        this.args = args;
    }
    public OIRExpression getObjectExpr() {
        return objectExpr;
    }
    public void setObjectExpr(OIRExpression objectExpr) {
        this.objectExpr = objectExpr;
    }
    public String getMethodName() {
        return methodName;
    }
    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }
    public ValueType getObjectType() {
        return objectType;
    }
    public List<OIRExpression> getArgs() {
        return args;
    }
    public void setArgs(List<OIRExpression> args) {
        this.args = args;
    }
    @Override
    public <S, T> T acceptVisitor(ASTVisitor<S, T> visitor, S state) {
        return visitor.visit(state, this);
    }
}
