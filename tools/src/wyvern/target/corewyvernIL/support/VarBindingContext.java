package wyvern.target.corewyvernIL.support;

import wyvern.target.corewyvernIL.type.ValueType;

public class VarBindingContext extends TypeContext {
	private TypeContext previous;
	private String varName;
	private ValueType type;

	public VarBindingContext(String var, ValueType type, TypeContext typeContext) {
		varName = var;
		this.type = type;
		previous = typeContext;
	}

	@Override
	public ValueType lookupType(String varName) {
		if (varName.equals(this.varName)) {
			return type;
		} else {
			return previous.lookupType(varName);
		}
	}

	@Override
	protected TypeContext getNext() {
		return previous;
	}
	
	@Override
	public String toString() {
		return "VarBindingContext[" + endToString();
	}
	
	@Override
	protected String endToString() {
		return "]";
	}
}
