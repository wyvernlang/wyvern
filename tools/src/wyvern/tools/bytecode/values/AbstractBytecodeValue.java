package wyvern.tools.bytecode.values;

public abstract class AbstractBytecodeValue<T> implements BytecodeValue {
	
	protected T value;
	
	public AbstractBytecodeValue(T v) {
		value = v;
	}
	
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
