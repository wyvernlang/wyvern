package wyvern.target.corewyvernIL.decl;

import wyvern.target.corewyvernIL.Environment;
import wyvern.target.corewyvernIL.emitIL;
import wyvern.target.corewyvernIL.astvisitor.EmitILVisitor;
import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.type.Type;
import wyvern.target.corewyvernIL.type.ValueType;

public class ValDeclaration extends Declaration implements emitIL {

	public ValDeclaration(String fieldName, ValueType type, Expression value) {
		super();
		this.fieldName = fieldName;
		this.type = type;
		this.value = value;
	}

	private String fieldName;
	private ValueType type;
	private Expression value;
	
	public String getFieldName() {
		return fieldName;
	}
	
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	
	public ValueType getType() {
		return type;
	}
	
	public void setType(ValueType type) {
		this.type = type;
	}
	
	public Expression getValue() {
		return value;
	}
	
	public void setValue(Expression value) {
		this.value = value;
	}

	@Override
	public String acceptEmitILVisitor(EmitILVisitor emitILVisitor,
			Environment env) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Type typeCheck(Environment env) {
		// TODO Auto-generated method stub
		return null;
	}
}
