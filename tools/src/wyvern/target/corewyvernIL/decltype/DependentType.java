package wyvern.target.corewyvernIL.decltype;

import wyvern.target.corewyvernIL.EmitOIR;
import wyvern.target.corewyvernIL.Environment;
import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.astvisitor.EmitOIRVisitor;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.target.oir.OIRAST;
import wyvern.target.oir.OIREnvironment;

public class DependentType extends DeclType implements EmitOIR{
	
	private String argument;
	private ValueType type;
	
	public DependentType(String argument, ValueType type) {
		super();
		this.argument = argument;
		this.type = type;
	}

	public String getArgument ()
	{
		return argument;
	}
	
	public void setArgument (String _arg)
	{
		argument = _arg;
	}
	
	public ValueType getType ()
	{
		return type;
	}
	
	public void setType (ValueType _type)
	{
		type = _type;
	}

	@Override
	public <T> T acceptVisitor(ASTVisitor <T> emitILVisitor,
			Environment env, OIREnvironment oirenv) {
		return emitILVisitor.visit(env, oirenv, this);
	}
}
