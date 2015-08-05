package wyvern.target.corewyvernIL.decl;

import wyvern.target.corewyvernIL.Environment;
import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.oir.OIREnvironment;

public abstract class NamedDeclaration extends Declaration {
	private String name;
	
	public NamedDeclaration(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

}
