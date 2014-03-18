package wyvern.tools.bytecode.values;

public class BytecodeEmptyVal implements BytecodeValue {

	@Override
	public BytecodeValue doInvoke(BytecodeValue operand, String op) {
		throw new RuntimeException("trying to do math with an empty val");
	}

	@Override
	public BytecodeValue dereference() {
		return this;
	}
	
	@Override
	public String toString() {
		return new String("undefined");
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof BytecodeEmptyVal) {
			return true;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		// temporary
		return "undefined".hashCode();
	}
}
