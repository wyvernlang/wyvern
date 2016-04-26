package wyvern.target.corewyvernIL.decl;

import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.FileLocation;

public abstract class DeclarationWithRHS extends NamedDeclaration {
	private Expression definition;
	
	public DeclarationWithRHS(String name, Expression definition, FileLocation loc) {
		super(name, loc);
		this.definition = definition;
	}

	public Expression getDefinition() {
		return definition;
	}
	
	public abstract ValueType getType();
	
	@Override
	public final DeclType typeCheck(TypeContext ctx, TypeContext thisCtx) {
		ValueType defType = definition.typeCheck(thisCtx); 
		if (!defType.isSubtypeOf(getType(), thisCtx))
			throw new RuntimeException("definition doesn't match declared type");
		return getDeclType();
	}
	
}
