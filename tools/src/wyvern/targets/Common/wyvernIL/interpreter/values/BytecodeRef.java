package wyvern.targets.Common.wyvernIL.interpreter.values;

public class BytecodeRef extends AbstractBytecodeValue<BytecodeValue> {
	
	public BytecodeRef(BytecodeValue v) {
		super(v);
	}
	
	//TODO currently set up to check if they reference the same value
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof BytecodeRef) {
			BytecodeRef bcr = (BytecodeRef) obj;
			return bcr.value.equals(value);
		} else if(obj instanceof BytecodeValue) {
			return value.equals(obj);
		}
		return false;
	}

	@Override
	public BytecodeValue doInvoke(BytecodeValue operand, String op) {
		return value.doInvoke(operand, op);
	}
	
	public void setValue(BytecodeValue newValue) {
		value = newValue;
	}
	
	@Override
	public BytecodeValue dereference() {
		return value.dereference();
	}
}
