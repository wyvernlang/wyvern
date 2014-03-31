package wyvern.targets.Common.wyvernIL.interpreter.values;

public class BytecodeString extends AbstractBytecodeValue<String> {

	public BytecodeString(String v) {
		super(v);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof BytecodeString)) {
			return false;
		}
		BytecodeString bci = (BytecodeString) obj;
		return value.equals(bci.value);
	}

	@Override
	public BytecodeValue doInvoke(BytecodeValue o, String op) {
		BytecodeString operand = (BytecodeString) o;
		switch (op) {
		case "+":
			return new BytecodeString(value + operand.value);
		
		// TODO temporary line: to be rewritten
		default: throw new RuntimeException("Bad string operation " + op);
		}
	}
}
