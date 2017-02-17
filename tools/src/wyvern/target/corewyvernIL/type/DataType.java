package wyvern.target.corewyvernIL.type;

import java.util.List;

import wyvern.target.corewyvernIL.Environment;
import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.expression.Path;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.oir.OIREnvironment;

public class DataType extends TagType {

	private List<Path> cases;

	// TODO: take a List<NominalType>, change the rest of the class accordingly
	public DataType(List<Path> cases, CaseType caseType) {
		super(caseType);
		this.cases = cases;
	}

	public List<Path> getCases ()
	{
		return cases;
	}

	@Override
	public void checkWellFormed(TypeContext ctx) {
		super.checkWellFormed(ctx);
		for (Path p:cases) {
			p.typeCheck(ctx);
		}
	}
	
  public <S, T> T acceptVisitor (ASTVisitor<S, T> emitILVisitor,
                                 S state) {
    return emitILVisitor.visit(state, this);
  }
}
