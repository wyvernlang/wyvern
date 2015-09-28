package wyvern.target.corewyvernIL.support;

import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.expression.IExpr;
import wyvern.target.corewyvernIL.expression.Variable;

public abstract class View {
	public abstract Variable adapt(Variable v);
	/*public String adapt(String varName) {
		return adapt(new Variable(varName)).getName();
	}*/
	public static View from(IExpr e, TypeContext ctx) {
		return new ReceiverView(e, ctx);
	}
}
