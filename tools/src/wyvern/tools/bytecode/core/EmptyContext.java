package wyvern.tools.bytecode.core;

import wyvern.tools.bytecode.values.BytecodeValue;

public class EmptyContext implements BytecodeContext {

	@Override
	public boolean existsInContext(BytecodeValue val) {
		return false;
	}

	@Override
	public boolean existsInContext(String val) {
		return false;
	}

	@Override
	public BytecodeValue getValue() {
		return null; // maybe switch this to an exception
	}
	
	@Override
	public String toString() {
		return "";
	}

}
