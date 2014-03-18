package wyvern.tools.bytecode.values;

/**
 * a basic skeleton for a BytecodeValue implementation
 * @author Tal Man
 *
 * @param <T>
 * 		the kind of value the class holds
 */
public abstract class AbstractBytecodeValue<T> implements BytecodeValue {
	
	protected T value;
	
	/**
	 * instantiate the value
	 * @param val
	 * 		the inner value for the BytecodeValue to hold
	 */
	public AbstractBytecodeValue(T val) {
		value = val;
	}
	
	/**
	 * get the value that this class holds
	 * @return
	 * 		the value that this class holds
	 */
	public T getValue() {
		return value;
	}
	
	@Override
	public abstract boolean equals(Object obj);
	
	@Override
	public int hashCode() {
		return value.hashCode();
	}
	
	@Override
	public String toString() {
		return "" + value;
	}

	@Override
	public abstract BytecodeValue doInvoke(BytecodeValue operand, String op);
	
	@Override
	public BytecodeValue dereference() {
		return this;
	}
}
