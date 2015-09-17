package wyvern.target.corewyvernIL.decl;

import wyvern.target.corewyvernIL.Environment;
import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.decltype.VarDeclType;
import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.type.Type;
import wyvern.target.corewyvernIL.type.ValueType;
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
	
	public abstract ValueType getType();
	public abstract DeclType getDeclType();
	
	@Override
	public final DeclType typeCheck(TypeContext ctx, TypeContext thisCtx) {
		if (!definition.typeCheck(ctx).isSubtypeOf(getType(), ctx))
			throw new RuntimeException("definition doesn't match declared type");
		return getDeclType();
	}
	
}
