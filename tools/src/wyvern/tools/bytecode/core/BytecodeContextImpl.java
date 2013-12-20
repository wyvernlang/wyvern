package wyvern.tools.bytecode.core;

import java.util.Map;

import wyvern.tools.bytecode.values.BytecodeValue;

public class BytecodeContextImpl implements BytecodeContext {
	
	private final BytecodeValue value;
	private final BytecodeContext inner;
	
	public BytecodeContextImpl(BytecodeValue v, BytecodeContext i) {
		value = v;
		inner = i;
	}

	@Override
	public boolean existsInContext(BytecodeValue val) {
		if(value.equals(val)) {
			return true;
		}
		return inner.existsInContext(val);
	}
	
	@Override
	public boolean existsInContext(String val) {
		if(value.getName().equals(val)) {
			return true;
		}
		return inner.existsInContext(val);
	}
	
	@Override
	public BytecodeValue getValue() {
		return value;
	}
	
	@Override
	public String toString() {
		return value.toString() + "\n" + inner.toString();
	}
}
