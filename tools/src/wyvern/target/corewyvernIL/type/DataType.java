package wyvern.target.corewyvernIL.type;

import java.util.List;

import wyvern.target.corewyvernIL.expression.Path;

public class DataType extends TagType {

	private List<Path> cases;

	public DataType(List<Path> cases, CaseType caseType) {
		super(caseType);
		this.cases = cases;
	}

	public List<Path> getCases ()
	{
		return cases;
	}
	
	public void setCases (List<Path> _cases)
	{
		cases = _cases;
	}
}
