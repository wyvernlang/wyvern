package wyvern.target.corewyvernIL.expression;

import wyvern.target.corewyvernIL.Environment;
import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.astvisitor.EmitOIRVisitor;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.target.oir.OIRAST;
import wyvern.target.oir.OIREnvironment;

public class FieldSet extends Expression {

	private Expression objectExpr;
	private String fieldName;
	private Expression exprToAssign;
	
	public FieldSet(ValueType exprType, Expression objectExpr,
			String fieldName, Expression exprToAssign) {
		super(exprType);
		this.objectExpr = objectExpr;
		this.fieldName = fieldName;
		this.exprToAssign = exprToAssign;
	}

	public Expression getObjectExpr() {
		return objectExpr;
	}
	
	public void setObjectExpr(Expression objectExpr) {
		this.objectExpr = objectExpr;
	}
	
	public String getFieldName() {
		return fieldName;
	}
	
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	
	public Expression getExprToAssign() {
		return exprToAssign;
	}
	
	public void setExprToAssign(Expression exprToAssign) {
		this.exprToAssign = exprToAssign;
	}

	@Override
	public ValueType typeCheck(wyvern.tools.types.Environment env) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T acceptVisitor(ASTVisitor <T> emitILVisitor,
			Environment env, OIREnvironment oirenv) {
		return emitILVisitor.visit(env, oirenv, this);
	}
}
