package wyvern.target.corewyvernIL.type;

import wyvern.target.corewyvernIL.support.TypeContext;

public class CaseOf extends CaseType {
	
	private ValueType valueType;
	
	// TODO: also take a NominalType, change the rest of the class accordingly
	public CaseOf(ValueType valueType) {
		super();
		this.valueType = valueType;
	}

	@Override
	public ValueType getValueType ()
	{
		return valueType;
	}

	@Override
	public void checkWellFormed(TypeContext ctx) {
		valueType.checkWellFormed(ctx);
	}	
}
