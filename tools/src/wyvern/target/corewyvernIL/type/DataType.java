package wyvern.target.corewyvernIL.type;

import java.util.List;

import wyvern.target.corewyvernIL.Environment;
import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.expression.Path;
import wyvern.target.oir.OIREnvironment;

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

  public <S, T> T acceptVisitor (ASTVisitor<S, T> emitILVisitor,
                                 S state) {
    return emitILVisitor.visit(state, this);
  }
}
