package wyvern.target.corewyvernIL.type;

public class CaseOf extends CaseType {
	
	private ValueType valueType;
	
	public CaseOf(ValueType valueType) {
		super();
		this.valueType = valueType;
	}

	public ValueType getValueType ()
	{
		return valueType;
	}	
}
