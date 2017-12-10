package wyvern.target.corewyvernIL.support;

import wyvern.target.corewyvernIL.expression.Value;
import wyvern.target.corewyvernIL.type.ValueType;

public abstract class EvalContext extends TypeContext {
	public EvalContext extend(String var, Value v) {
		return new VarEvalContext(var, v, this);
	}

	@Override
	public ValueType lookupTypeOf(String varName) {
		return lookupValue(varName).getType();
	}

	@Override
	public String toString() {
		return "EvalContext[" + endToString();
	}
	
	public abstract Value lookupValue(String varName);
	
	public static EvalContext empty() {
		return theEmpty;
	}
	
	private static EvalContext theEmpty = new EmptyValContext();

	public abstract String endToString();
}
