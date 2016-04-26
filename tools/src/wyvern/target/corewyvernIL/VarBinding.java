package wyvern.target.corewyvernIL;

import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.type.ValueType;

public class VarBinding {
	private String varName;
	private ValueType type;
	private Expression expr;
	
	public VarBinding(String varName, ValueType type, Expression expr) {
		this.varName = varName;
		this.type = type;
		this.expr = expr;
		if (expr == null) throw new RuntimeException();
	}

	public String getVarName() {
		return varName;
	}

	public ValueType getType() {
		return type;
	}

	public Expression getExpression() {
		return expr;
	}
}
