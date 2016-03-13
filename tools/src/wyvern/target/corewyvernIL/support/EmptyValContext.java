package wyvern.target.corewyvernIL.support;

import wyvern.target.corewyvernIL.expression.Value;

public class EmptyValContext extends EvalContext {
	public Value lookupValue(String varName) {
		throw new RuntimeException("Variable " + varName + " not found");
	}

	@Override
	public EvalContext combine(EvalContext ctx) {
		return ctx;
	}

	@Override
	public String endToString() {
		return null;
	}

	@Override
	protected TypeContext getNext() {
		return null;
	}
}
