package wyvern.target.corewyvernIL.support;

import wyvern.target.corewyvernIL.expression.Value;
import wyvern.target.corewyvernIL.type.ValueType;

public abstract class EvalContext extends TypeContext {
	public EvalContext extend(String var, Value v) {
		return new VarEvalContext(var, v, this);
	}

	@Override
	public ValueType lookupType(String varName) {
		// TODO: return appropriate ValueType
		throw new RuntimeException("Cannot look up type from EvalContext.");
	}

	public abstract Value lookupValue(String varName);
	
	public abstract EvalContext combine(EvalContext ctx);
	
	public static EvalContext empty() {
		return theEmpty;
	}
	
	private static EvalContext theEmpty = new EmptyValContext();

	public abstract String endToString();
}
