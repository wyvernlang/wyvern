package wyvern.target.corewyvernIL.expression;

import java.util.HashSet;
import java.util.Set;

import wyvern.target.corewyvernIL.support.EvalContext;
import wyvern.target.corewyvernIL.type.ValueType;

public abstract class AbstractValue extends Expression implements Value {
	protected AbstractValue(ValueType exprType) {
		super(exprType);
	}


	@Override
	public Value interpret(EvalContext ctx) {
		return this;
	}
	
}
