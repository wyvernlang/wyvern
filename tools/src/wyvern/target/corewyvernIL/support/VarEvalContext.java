package wyvern.target.corewyvernIL.support;

import wyvern.target.corewyvernIL.expression.Value;
import wyvern.target.corewyvernIL.type.ValueType;

public class VarEvalContext extends EvalContext {
	private EvalContext previous;
	private String varName;
	private Value v;

	public VarEvalContext(String var, Value v, EvalContext evalContext) {
		varName = var;
		this.v = v;
		previous = evalContext;
	}

	@Override
	public Value lookup(String varName) {
		if (varName.equals(this.varName)) {
			return v;
		} else {
			return previous.lookup(varName);
		}
	}
}
