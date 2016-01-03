package wyvern.tools.typedAST.interfaces;

import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.support.CallableExprGenerator;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.support.TopLevelContext;

public interface ExpressionAST extends TypedAST {

	/** Generates the new Wyvern Intermediate Language (IL) from this
	 * AST.
	 * 
	 * @param ctx The generation context used for translation
	 * @return
	 */
	Expression generateIL(GenContext ctx);
	
	CallableExprGenerator getCallableExpr(GenContext ctx);
	
	@Override
	public default void genTopLevel(TopLevelContext topLevelContext) {
		topLevelContext.addExpression(generateIL(topLevelContext.getContext()));
	}

}
