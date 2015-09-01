package wyvern.target.corewyvernIL.decltype;

import wyvern.target.corewyvernIL.ASTNode;
import wyvern.target.corewyvernIL.EmitOIR;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.support.View;

public abstract class DeclType extends ASTNode implements EmitOIR {
	private String name;
	
	DeclType(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	public abstract boolean isSubtypeOf(DeclType dt, TypeContext ctx);

	public abstract DeclType adapt(View v);
}
