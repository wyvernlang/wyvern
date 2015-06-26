package wyvern.target.corewyvernIL.expression;

import wyvern.target.corewyvernIL.Environment;
import wyvern.target.corewyvernIL.astvisitor.EmitILVisitor;
import wyvern.target.corewyvernIL.type.ValueType;

public class FieldGet extends Expression implements Path {

	private Expression objectExpr;
	private String fieldName;
	
	public FieldGet(Expression objectExpr, String fieldName) {
		super();
		this.objectExpr = objectExpr;
		this.fieldName = fieldName;
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

	@Override
	public java.lang.String acceptEmitILVisitor(EmitILVisitor emitILVisitor,
			Environment env) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ValueType typeCheck(wyvern.tools.types.Environment env) {
		// TODO Auto-generated method stub
		return null;
	}
}