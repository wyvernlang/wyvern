package wyvern.tools.bytecode.values;

public interface BytecodeValue extends Cloneable {

	public BytecodeValue doInvoke(BytecodeValue operand, String op);
	
	/**
	 * specialty case for when the operand is a reference, this function
	 * will first dereference it then call the regular doInvoke
	 * @param operand
	 * 		the reference object
	 * @param op
	 * 		the operation to be done on it
	 * @return
	 * 		the object representing the result
	 */
	public BytecodeValue doInvoke(BytecodeRef operand, String op);
}
