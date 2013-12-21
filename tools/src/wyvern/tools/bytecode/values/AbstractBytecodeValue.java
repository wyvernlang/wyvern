package wyvern.tools.bytecode.values;

public abstract class AbstractBytecodeValue<T> implements BytecodeValue {
	
	protected final T value;
	protected final String name;
	
	public AbstractBytecodeValue(T v, String n) {
		value = v;
		name = new String(n);
	}
	
	public T getValue() {
		return value;
	}
	
	public String getName() {
		return new String(name);
	}
	
	@Override
	public int hashCode() {
		return name.hashCode();
	}
	
	@Override
	public String toString() {
		return name + " = " + value;
	}

	@Override
	public abstract BytecodeValue doInvoke(BytecodeValue operand, String op);
}
