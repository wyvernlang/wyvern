package wyvern.target.corewyvernIL.support;

import wyvern.target.corewyvernIL.expression.Value;

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
	public Value lookupValue(String varName) {
		if (varName.equals(this.varName)) {
			return v;
		} else {
			return previous.lookupValue(varName);
		}
	}

	@Override
	public EvalContext combine(EvalContext ctx) {
		if(ctx instanceof EmptyValContext) {
			return this;
		} else {
			// must be VarEvalContext
			VarEvalContext vCtx = (VarEvalContext) ctx;
			return this.extend(vCtx.varName, vCtx.v).combine(vCtx.previous);
		}
	}
	
	@Override
	public String toString() {
		return "EvalContext[" + endToString();
	}
	
	@Override
	public String endToString() {
		return varName  + " = " + v + ", " + previous.endToString();
	}

	@Override
	protected TypeContext getNext() {
		return previous;
	}
}
