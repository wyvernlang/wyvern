package wyvern.target.corewyvernIL.expression;

import wyvern.target.corewyvernIL.EmitOIR;
import wyvern.target.corewyvernIL.support.EvalContext;
import wyvern.target.corewyvernIL.type.ValueType;

public abstract class Value extends Expression {
	protected Value(ValueType exprType) {
		super(exprType);
	}

	@Override
	public Value interpret(EvalContext ctx) {
		return this;
	}
}
