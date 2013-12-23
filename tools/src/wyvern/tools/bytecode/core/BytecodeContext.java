package wyvern.tools.bytecode.core;

import wyvern.tools.bytecode.values.BytecodeValue;

public interface BytecodeContext {
		
	public boolean existsInContext(String valName);
	
	public BytecodeValue getValue(String valName);
	
	public String getLastEnteredName();
	
	public BytecodeValue getLastEnteredValue();
	
	public BytecodeContext clone();

	// for testing purposes only
	public String toSimpleString();
	
}
