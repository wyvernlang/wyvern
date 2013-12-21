package wyvern.tools.bytecode.values;

public class BytecodeBoolean extends AbstractBytecodeValue<Boolean> {

	public BytecodeBoolean(Boolean v, String n) {
		super(v,n);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof BytecodeBoolean)) {
			return false;
		}
		BytecodeBoolean bci = (BytecodeBoolean) obj;
		return name.equals(bci.getName());
	}

	@Override
	public BytecodeValue doInvoke(BytecodeValue o, String op) {
		BytecodeBoolean operand = (BytecodeBoolean) o;
		switch (op) {
		case "||":
			return new BytecodeBoolean(operand.value || value, name);
		case "&&":
			return new BytecodeBoolean(operand.value && value, name);
		
		// TODO temporary line: to be rewritten
		default: throw new RuntimeException("Bad boolean operation " + op);
		}
	}
}
