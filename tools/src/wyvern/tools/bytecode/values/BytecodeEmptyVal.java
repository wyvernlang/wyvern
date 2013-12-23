package wyvern.tools.bytecode.values;

public class BytecodeEmptyVal implements BytecodeValue {

	@Override
	public BytecodeValue doInvoke(BytecodeValue operand, String op) {
		throw new RuntimeException("trying to do math with an empty val");
	}

	@Override
	public BytecodeValue dereference() {
		throw new RuntimeException("trying to derefernce an empty val");
	}
	
	@Override
	public String toString() {
		return new String("context is empty");
	}

}
