package wyvern.target.corewyvernIL.support;

import wyvern.target.corewyvernIL.type.ValueType;

public class EmptyTypeContext extends TypeContext {
	public ValueType lookup(String varName) {
		throw new RuntimeException("Variable " + varName + " not found");
	}

	@Override
	protected TypeContext getNext() {
		return null;
	}
}
