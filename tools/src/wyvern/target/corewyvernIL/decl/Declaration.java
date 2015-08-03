package wyvern.target.corewyvernIL.decl;

import wyvern.target.corewyvernIL.ASTNode;
import wyvern.target.corewyvernIL.EmitOIR;
import wyvern.target.corewyvernIL.Environment;
import wyvern.target.corewyvernIL.astvisitor.EmitOIRVisitor;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.support.EvalContext;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.type.Type;
import wyvern.target.corewyvernIL.type.ValueType;

public abstract class Declaration extends ASTNode implements EmitOIR {
	public abstract DeclType typeCheck(TypeContext ctx);

	/**
	 * Interprets val and var declarations down to a value binding.
	 * For other declarations this just returns the receiver
	 */
	public Declaration interpret(EvalContext ctx) {
		return this;
	}
}
