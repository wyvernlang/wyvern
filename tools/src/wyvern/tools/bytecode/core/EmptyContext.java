package wyvern.tools.bytecode.core;

import wyvern.tools.bytecode.values.BytecodeEmptyVal;
import wyvern.tools.bytecode.values.BytecodeValue;

public class EmptyContext implements BytecodeContext {

	@Override
	public boolean existsInContext(String val) {
		return false;
	}

	@Override
	public BytecodeValue getLastEnteredValue() {
		return new BytecodeEmptyVal();
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

	@Override
	public String getLastEnteredName() {
		return "";
	}
	
	@Override
	public EmptyContext clone() {
		return new EmptyContext();
	}

	@Override
	public String toSimpleString() {
		return "";
	}
	

}
