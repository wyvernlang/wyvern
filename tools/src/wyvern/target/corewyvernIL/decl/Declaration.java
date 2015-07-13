package wyvern.target.corewyvernIL.decl;

import wyvern.target.corewyvernIL.ASTNode;
import wyvern.target.corewyvernIL.EmitOIR;
import wyvern.target.corewyvernIL.Environment;
import wyvern.target.corewyvernIL.astvisitor.EmitOIRVisitor;
import wyvern.target.corewyvernIL.type.Type;

public abstract class Declaration extends ASTNode implements EmitOIR {
	public abstract Type typeCheck (Environment env);
}
