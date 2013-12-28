package wyvern.tools.bytecode.values;

import wyvern.tools.bytecode.core.BytecodeContext;

public class BytecodeClass implements BytecodeValue {

	protected final BytecodeContext coreContext;
	
	public BytecodeClass(BytecodeContext context) {
		coreContext = context;
	}
	
	public BytecodeValue getValue(String name) {
		return coreContext.getValue(name);
	}

	@Override
	public BytecodeValue doInvoke(BytecodeValue operand, String op) {
		throw new RuntimeException("trying to do math with classes");
	}

	@Override
	public BytecodeValue dereference() {
		return this;
	}
}
