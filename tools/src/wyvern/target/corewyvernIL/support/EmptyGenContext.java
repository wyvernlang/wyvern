package wyvern.target.corewyvernIL.support;

import wyvern.target.corewyvernIL.type.ValueType;

public class EmptyGenContext extends GenContext {

	protected EmptyGenContext() {
		super(null);
	}

	@Override
	public ValueType lookup(String varName) {
		throw new RuntimeException("Variable " + varName + " not found");
	}

	@Override
	String endToString() {
		return "]";
	}

	@Override
	public String getContainerForTypeAbbrev(String typeName) {
		return null;
		//throw new RuntimeException("Type " + varName + " not found");
	}

	@Override
	public CallableExprGenerator getCallableExprRec(String varName, GenContext origCtx) {
		throw new RuntimeException("Variable " + varName + " not found");
	}

}
