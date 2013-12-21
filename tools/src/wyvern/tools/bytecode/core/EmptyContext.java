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
	public BytecodeValue getLastEnteredValue() {
		return null; // maybe switch this to an exception
	}
	
	@Override
	public String toString() {
		return "";
	}

	@Override
	public BytecodeValue getValue(String val) {
		//TODO rework this
		throw new RuntimeException("Value not found");
	}

}
