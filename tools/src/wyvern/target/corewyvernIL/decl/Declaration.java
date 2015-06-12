package wyvern.target.corewyvernIL.decl;

import wyvern.target.corewyvernIL.ASTNode;
import wyvern.target.corewyvernIL.Environment;
import wyvern.target.corewyvernIL.type.Type;

public abstract class Declaration extends ASTNode {
	public abstract Type typeCheck (Environment env);
}
