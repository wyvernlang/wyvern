package wyvern.target.oir.expressions;

import java.util.*;

import wyvern.target.oir.ASTVisitor;
import wyvern.target.oir.OIREnvironment;
import wyvern.target.oir.declarations.OIRClassDeclaration;
import wyvern.target.oir.declarations.OIRInterface;
import wyvern.target.oir.declarations.OIRType;

public class OIRMethodCall extends OIRExpression{
	private OIRExpression objectExpr;
	private String methodName;
	private List<OIRExpression> args;
	@Override
	public OIRType typeCheck(OIREnvironment oirEnv) {
		OIRType type = objectExpr.typeCheck(oirEnv);
		if (type instanceof OIRClassDeclaration)
		{
			OIRType oirType;
			
			oirType = ((OIRClassDeclaration)type).getTypeForMember(methodName);
			if (oirType == null)
			{
				/* TODO: Error */
			}
			setExprType (oirType);
			return getExprType ();
		}
		else if (type instanceof OIRInterface)
		{
			setExprType (((OIRInterface)type).getTypeForMember(methodName));
			return getExprType ();
		}
		/* TODO: Throw type is not class */
		return null;
	}
	public OIRMethodCall(OIRExpression objectExpr, String methodName,
			List<OIRExpression> args) {
		super();
		this.objectExpr = objectExpr;
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
	public List<OIRExpression> getArgs() {
		return args;
	}
	public void setArgs(List<OIRExpression> args) {
		this.args = args;
	}
	@Override
	public <T> T acceptVisitor(ASTVisitor<T> visitor, OIREnvironment oirenv) {
		return visitor.visit(oirenv, this);
	}
}
