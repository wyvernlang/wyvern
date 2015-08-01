package wyvern.target.corewyvernIL.decl;

import wyvern.target.corewyvernIL.Environment;
import wyvern.target.corewyvernIL.EmitOIR;
import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.astvisitor.EmitOIRVisitor;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.expression.Value;
import wyvern.target.corewyvernIL.type.Type;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.target.oir.OIRAST;
import wyvern.target.oir.OIREnvironment;
import wyvern.target.corewyvernIL.expression.Variable;
import wyvern.target.corewyvernIL.support.TypeContext;

public class VarDeclaration extends Declaration {

	private String fieldName;
	private ValueType type;
	private Variable value;
	
	public VarDeclaration(String fieldName, ValueType type, Variable value) {
		super();
		this.fieldName = fieldName;
		this.type = type;
		this.value = value;
	}
	
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
	
	public Variable getValue() {
		return value;
	}
	
	public void setValue(Variable value) {
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
