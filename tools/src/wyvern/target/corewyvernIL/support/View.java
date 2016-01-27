package wyvern.target.corewyvernIL.support;

import wyvern.target.corewyvernIL.expression.IExpr;
import wyvern.target.corewyvernIL.expression.Path;
import wyvern.target.corewyvernIL.expression.Variable;

public abstract class View {
	/**
	 * Returns the Path that is equivalent to variable v
	 * under this View.  For example, if this view maps x to
	 * y.f, then adapt(x) will return y.f, and adapt(z)
	 * will return z. 
	 */
	public abstract Path adapt(Variable v);
	/*public String adapt(String varName) {
		return adapt(new Variable(varName)).getName();
	}*/
	public static View from(IExpr e, TypeContext ctx) {
		return new ReceiverView(e, ctx);
	}
}
