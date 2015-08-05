package wyvern.target.corewyvernIL.decl;

import wyvern.target.corewyvernIL.Environment;
import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.oir.OIREnvironment;

public abstract class DeclarationWithRHS extends NamedDeclaration {
	private Expression definition;
	
	public DeclarationWithRHS(String name, Expression definition) {
		super(name);
		this.definition = definition;
	}

	public Expression getDefinition() {
		return definition;
	}
}
