package wyvern.targets.Common.wyvernIL.interpreter.values;

public class BytecodeBoolean extends AbstractBytecodeValue<Boolean> {

	public BytecodeBoolean(Boolean v) {
		super(v);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof BytecodeBoolean)) {
			return false;
		}
		BytecodeBoolean bci = (BytecodeBoolean) obj;
		return value.equals(bci.value);
	}

	@Override
	public BytecodeValue doInvoke(BytecodeValue o, String op) {
		BytecodeBoolean operand = (BytecodeBoolean) o;
		switch (op) {
		case "||":
			return new BytecodeBoolean(operand.value || value);
		case "&&":
			return new BytecodeBoolean(operand.value && value);
		
		// TODO temporary line: to be rewritten
		default: throw new RuntimeException("Bad boolean operation " + op);
		}
	}
}
