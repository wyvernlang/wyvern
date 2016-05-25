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

  public <T, E> T acceptVisitor (ASTVisitor<T, E> emitILVisitor,
                                 E env,
                                 OIREnvironment oirenv) {
    return emitILVisitor.visit(env, oirenv, this);
  }
}
