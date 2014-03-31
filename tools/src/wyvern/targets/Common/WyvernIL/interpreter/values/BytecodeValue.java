package wyvern.targets.Common.wyvernIL.interpreter.values;

public interface BytecodeValue {

	/**
	 * does an operation on the value (such as math or logic)
	 * @param operand
	 * 		the other value used in the computation
	 * @param op
	 * 		the operator to be used in the computation
	 * @return
	 * 		a new BytecodeValue representing the computations result
	 * @throws RuntimeException
	 * 		when trying to use an operation on a subtype for which it's undefied
	 */
	public BytecodeValue doInvoke(BytecodeValue operand, String op);
	
	/**
	 * dereferences the value, if it is a reference returns the inner value
	 * and if it is not it returns itself unchanged
	 * @return
	 * 		the dereferenced object
	 */
	public BytecodeValue dereference();
}
