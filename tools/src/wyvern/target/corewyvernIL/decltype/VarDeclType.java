package wyvern.target.corewyvernIL.decltype;

import wyvern.target.corewyvernIL.type.ValueType;

public class VarDeclType extends DeclType {
	
	private String field;
	private ValueType type;
	
	public VarDeclType(String field, ValueType type) {
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
}
