package wyvern.target.corewyvernIL.decl;

import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.type.ValueType;

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
		ValueType defType = definition.typeCheck(ctx); 
		if (!defType.isSubtypeOf(getType(), ctx))
			throw new RuntimeException("definition doesn't match declared type");
		return getDeclType();
	}
	
}
