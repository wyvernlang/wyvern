package wyvern.target.corewyvernIL.type;

import wyvern.target.corewyvernIL.Environment;
import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.oir.OIREnvironment;

public class CaseType extends Type{
  public <T> T acceptVisitor (ASTVisitor<T> emitILVisitor,
                              Environment env,
                              OIREnvironment oirenv) {
    return emitILVisitor.visit(env, oirenv, this);
  }
}
