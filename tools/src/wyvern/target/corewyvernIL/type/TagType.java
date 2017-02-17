package wyvern.target.corewyvernIL.type;

import wyvern.target.corewyvernIL.support.TypeContext;

public abstract class TagType extends Type {
	
	protected CaseType caseType;
	
	public TagType(CaseType caseType) {
		super();
		this.caseType = caseType;
	}

	public CaseType getCaseType()
	{
		return caseType;
	}
	
	@Override
	public ValueType getValueType()
	{
		return caseType.getValueType();
	}
	
	@Override
	public void checkWellFormed(TypeContext ctx) {
		caseType.checkWellFormed(ctx);
	}
	
}
