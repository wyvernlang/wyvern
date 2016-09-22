package wyvern.tools.typedAST.interfaces;

import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.expression.IExpr;
import wyvern.target.corewyvernIL.modules.TypedModuleSpec;
import wyvern.target.corewyvernIL.support.CallableExprGenerator;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.support.TopLevelContext;
import wyvern.target.corewyvernIL.type.ValueType;
import java.util.List;

public interface ExpressionAST extends TypedAST {

	/** Generates the new Wyvern Intermediate Language (IL) from this
	 * AST.
	 * 
	 * @param ctx The generation context used for translation
	 * @param expectedType TODO
	 * @param dependencies TODO
	 * @return
	 */
	IExpr generateIL(GenContext ctx, ValueType expectedType, List<TypedModuleSpec> dependencies);
	
	CallableExprGenerator getCallableExpr(GenContext ctx);
	
	@Override
	public default void genTopLevel(TopLevelContext topLevelContext) {
		final IExpr exp = generateIL(topLevelContext.getContext(), null, topLevelContext.getDependencies());
		ValueType type = exp.typeCheck(topLevelContext.getContext());
		topLevelContext.addExpression(exp, type);
		Expression e;
	}
}
