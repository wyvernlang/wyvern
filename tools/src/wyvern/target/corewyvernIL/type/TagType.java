package wyvern.target.corewyvernIL.type;

import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.support.View;

public abstract class TagType extends Type {
	
	private ValueType valueType;
	protected NominalType parentType;
	
	public TagType(NominalType parentType, ValueType valueType) {
		super();
		this.valueType = valueType;
		this.parentType = parentType;
	}

	public NominalType getParentType(View v) {
		return (NominalType)parentType.adapt(v);
	}
	
	@Override
	public ValueType getValueType()
	{
		return valueType;
	}
	
	@Override
	public void checkWellFormed(TypeContext ctx) {
		valueType.checkWellFormed(ctx);
		if (parentType != null)
			parentType.checkWellFormed(ctx);
	}	
}
