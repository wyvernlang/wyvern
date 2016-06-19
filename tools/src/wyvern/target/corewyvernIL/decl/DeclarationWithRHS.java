package wyvern.target.corewyvernIL.decl;

import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.expression.IExpr;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.ToolError;

public abstract class DeclarationWithRHS extends NamedDeclaration {
	private IExpr definition;
	
	public DeclarationWithRHS(String name, IExpr definition, FileLocation loc) {
		super(name, loc);
		this.definition = definition;
	}

	public IExpr getDefinition() {
		return (Expression) definition;
	}
	
	public abstract ValueType getType();
	
	@Override
	public final DeclType typeCheck(TypeContext ctx, TypeContext thisCtx) {
		ValueType defType = definition.typeCheck(thisCtx); 
		if (!defType.isSubtypeOf(getType(), thisCtx))
			ToolError.reportError(ErrorMessage.ASSIGNMENT_SUBTYPING, this);
		return getDeclType();
	}
	
}
