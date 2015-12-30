package wyvern.target.corewyvernIL.type;

public abstract class TagType extends Type {
	
	protected CaseType caseType;
	
	public TagType(CaseType caseType) {
		super();
		this.caseType = caseType;
	}

	public CaseType getCaseType ()
	{
		return caseType;
	}
}
