package wyvern.tools.bytecode.values;

public interface BytecodeValue extends Cloneable {

	public BytecodeValue doInvoke(BytecodeValue operand, String op);
	
	public BytecodeValue dereference();
}
