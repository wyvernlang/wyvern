package wyvern.tools.bytecode.core;

import java.util.Map;

import wyvern.tools.bytecode.values.BytecodeValue;

public interface BytecodeContext {
	
	public boolean existsInContext(BytecodeValue val);
	
	public boolean existsInContext(String val);
	
	public BytecodeValue getValue();
	
}
