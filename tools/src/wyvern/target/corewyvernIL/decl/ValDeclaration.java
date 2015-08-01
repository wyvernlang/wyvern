package wyvern.target.corewyvernIL.decl;

import wyvern.target.corewyvernIL.Environment;
import wyvern.target.corewyvernIL.EmitOIR;
import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.astvisitor.EmitOIRVisitor;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.type.Type;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.target.oir.OIRAST;
import wyvern.target.oir.OIREnvironment;

public class ValDeclaration extends Declaration {

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
	public <T> T acceptVisitor(ASTVisitor <T> emitILVisitor,
			Environment env, OIREnvironment oirenv) {
		return emitILVisitor.visit(env, oirenv, this);
	}

	@Override
	public DeclType typeCheck(TypeContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}
}
