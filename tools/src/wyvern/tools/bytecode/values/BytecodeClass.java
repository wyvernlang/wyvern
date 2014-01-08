package wyvern.tools.bytecode.values;

import wyvern.tools.bytecode.core.BytecodeContext;

public class BytecodeClass implements BytecodeValue {

	protected final BytecodeContext coreContext;
	
	/**
	 * instantiates a new class instance
	 * @param context
	 * 		the context to be used in the class
	 */
	public BytecodeClass(BytecodeContext context) {
		coreContext = context;
	}
	
	/**
	 * finds and returns a value in a class
	 * @param name
	 * 		the name of the value to be found
	 * @return
	 * 		the value from the context
	 */
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
	
	public BytecodeContext getContext() {
		return coreContext;
	}
	
	@Override
	public String toString() {
		return "a class instance";
	}
}
