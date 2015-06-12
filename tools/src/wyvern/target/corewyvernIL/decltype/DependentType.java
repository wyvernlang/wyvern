package wyvern.target.corewyvernIL.decltype;

import wyvern.target.corewyvernIL.type.ValueType;

public class DependentType extends DeclType {
	
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
}
