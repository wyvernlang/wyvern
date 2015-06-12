package wyvern.target.corewyvernIL.type;

public class TypeApplication extends ValueType {

	private ValueType baseType;
	private Type typeArgument;
	private String typeMember;
	
	
	public TypeApplication(ValueType baseType, Type typeArgument,
			String typeMember) {
		super();
		this.baseType = baseType;
		this.typeArgument = typeArgument;
		this.typeMember = typeMember;
	}

	public void setBaseType(ValueType baseType) {
		this.baseType = baseType;
	}

	public void setTypeArgument(Type typeArgument) {
		this.typeArgument = typeArgument;
	}

	public void setTypeMember(String typeMember) {
		this.typeMember = typeMember;
	}
	
	public ValueType getBaseType ()
	{
		return baseType;
	}
	
	public Type getTypeArgument ()
	{
		return typeArgument;
	}
	
	public String getTypeMember()
	{
		return typeMember;
	}
}
