package wyvern.target.corewyvernIL.decltype;

import wyvern.target.corewyvernIL.ASTNode;
import wyvern.target.corewyvernIL.EmitOIR;
import wyvern.target.corewyvernIL.support.TypeContext;

public abstract class DeclType extends ASTNode implements EmitOIR {

	public abstract boolean isSubtypeOf(DeclType dt, TypeContext ctx);
}
