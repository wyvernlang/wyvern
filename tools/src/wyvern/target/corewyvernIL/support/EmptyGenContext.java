package wyvern.target.corewyvernIL.support;

import wyvern.target.corewyvernIL.type.ValueType;

public class EmptyGenContext extends GenContext {
	private InterpreterState state;
	
	protected EmptyGenContext() {
		super(null);
	}

	public EmptyGenContext(InterpreterState state) {
		super(null);
		this.state = state;
	}

	@Override
	public ValueType lookupType(String varName) {
		throw new RuntimeException("Variable " + varName + " not found");
	}

	@Override
	protected String endToString() {
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

	@Override
	public InterpreterState getInterpreterState() {
		return state;
	}
}
