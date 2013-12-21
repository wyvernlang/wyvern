package wyvern.tools.bytecode.values;

public interface BytecodeValue extends Cloneable {

	public String getName();
	
	public BytecodeValue doInvoke(BytecodeValue operand, String op);
	
}
