package wyvern.target.corewyvernIL.type;

import wyvern.target.corewyvernIL.Environment;
import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.oir.OIREnvironment;

public class ExtensibleTagType extends TagType {

	public ExtensibleTagType(CaseType caseType) {
		super(caseType);
	}

  public <T, E> T acceptVisitor(ASTVisitor<T, E> emitILVisitor,
                                E env,
                                OIREnvironment oirenv) {
    return emitILVisitor.visit(env, oirenv, this);
  }
}
