package wyvern.target.corewyvernIL.support;

import java.util.List;

import wyvern.target.corewyvernIL.FormalArg;
import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.tools.errors.HasLocation;

/**
 * This abstraction allows more efficient code generation by
 * postponing generation of code that refers to a method
 * until we find out whether a method call is adjacent.  In
 * other words calling genExprWithArgs() generates more
 * efficient code than calling genExpr() followed by calling
 * "apply()" in the case where genExpr() would have to do
 * eta-expansion of the method.
 * 
 * @author aldrich
 */
public interface CallableExprGenerator {
	public Expression genExpr();

	public Expression genExprWithArgs(List<Expression> args, HasLocation loc);
	
	/** Returns null if no argument type is expected */
	public List<FormalArg> getExpectedArgTypes(TypeContext ctx);

}
