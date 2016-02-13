package wyvern.target.corewyvernIL.support;

import wyvern.target.corewyvernIL.expression.Value;

public abstract class EvalContext {
	public EvalContext extend(String var, Value v) {
		return new VarEvalContext(var, v, this);
	}
	
	public abstract Value lookup(String varName);
	
	public abstract EvalContext combine(EvalContext ctx);
	
	public static EvalContext empty() {
		return theEmpty;
	}
	
	private static EvalContext theEmpty = new EmptyValContext();

	public abstract String endToString();
}
