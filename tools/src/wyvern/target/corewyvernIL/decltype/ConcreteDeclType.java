package wyvern.target.corewyvernIL.decltype;

import wyvern.target.corewyvernIL.type.ValueType;


public class ConcreteDeclType extends DeclType {
	
	private String typeName;
	private ValueType sourceType;
	
	public ConcreteDeclType(String typeName, ValueType sourceType) {
		super();
		this.typeName = typeName;
		this.sourceType = sourceType;
	}

	public String getTypeName ()
	{
		return typeName;
	}
	
	public void setTypeName (String _typeName)
	{
		typeName = _typeName;
	}
	
	public void setSourceType (ValueType _type)
	{
		sourceType = _type;
	}
	
	public ValueType getSourceType ()
	{
		return sourceType;
	}
}
