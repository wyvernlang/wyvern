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
	public ValueType lookup(String varName) {
		if (varName.equals(this.varName)) {
			return type;
		} else {
			return previous.lookup(varName);
		}
	}
}
