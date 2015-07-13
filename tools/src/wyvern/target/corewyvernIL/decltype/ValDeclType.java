package wyvern.target.corewyvernIL.decltype;

import wyvern.target.corewyvernIL.EmitOIR;
import wyvern.target.corewyvernIL.Environment;
import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.astvisitor.EmitOIRVisitor;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.target.oir.OIRAST;
import wyvern.target.oir.OIREnvironment;

public class ValDeclType extends DeclType implements EmitOIR{

	private String field;
	private ValueType type;
	
	public ValDeclType(String field, ValueType type) {
		super();
		this.field = field;
		this.type = type;
	}

	public String getField ()
	{
		return field;
	}
	
	public void setField (String _field)
	{
		field = _field;
	}
	
	public void setType (ValueType _type)
	{
		type = _type;
	}
	
	public ValueType getType ()
	{
		return type;
	}

	@Override
	public <T> T acceptVisitor(ASTVisitor <T> emitILVisitor,
			Environment env, OIREnvironment oirenv) {
		return emitILVisitor.visit(env, oirenv, this);
	}
}
