package wyvern.target.corewyvernIL;

import wyvern.target.corewyvernIL.expression.Expression;

public class VarBinding {
	private String varName;
	private Expression expr;
	
	public VarBinding(String varName, Expression expr) {
		this.varName = varName;
		this.expr = expr;
		if (expr == null) throw new RuntimeException();
	}

	public String getVarName() {
		return varName;
	}

	public Expression getExpression() {
		return expr;
	}
}
