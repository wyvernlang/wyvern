package wyvern.tools.bytecode.core;

import wyvern.tools.bytecode.values.BytecodeValue;

public class BytecodeContextImpl implements BytecodeContext {
	
	private final String name;
	private final BytecodeValue value;
	private final BytecodeContext inner;
	
	public BytecodeContextImpl(BytecodeValue v, String n, BytecodeContext i) {
		name = n;
		value = v;
		inner = i;
	}

	@Override
	public boolean existsInContext(String val) {
		if(name.equals(val)) {
			return true;
		}
		return inner.existsInContext(val);
	}
	
	@Override
	public BytecodeValue getLastEnteredValue() {
		return value;
	}
	
	@Override
	public String toString() {
		return name + ": " + value.toString() + "\n" + inner.toString();
	}
	
	public String toSimpleString() {
		if(name.startsWith("temp$")) {
			return inner.toSimpleString();
		}
		return name + ": " + value.toString() + "\n" + inner.toSimpleString();
	}

	@Override
	public BytecodeValue getValue(String val) {
		if(name.equals(val)) {
			return value;
		} 
		return inner.getValue(val);
	}

	@Override
	public String getLastEnteredName() {
		return name;
	}
	
	@Override
	public BytecodeContextImpl clone() {
		BytecodeContext newInner = inner.clone();
		return new BytecodeContextImpl(value,name,newInner);
	}
}
