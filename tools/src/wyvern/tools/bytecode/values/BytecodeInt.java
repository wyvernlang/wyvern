package wyvern.tools.bytecode.values;

public class BytecodeInt extends AbstractBytecodeValue<Integer> {

	public BytecodeInt(int v, String n) {
		super(v, n);
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof BytecodeInt)) {
			return false;
		}
		BytecodeInt bci = (BytecodeInt) obj;
		return name.equals(bci.getName());
	}

	@Override
	public BytecodeValue doInvoke(BytecodeValue o, String op) {
		BytecodeInt operand = (BytecodeInt) o;
		switch (op) {
		case "+":
			return new BytecodeInt(value + operand.value, name);
		case "-":
			return new BytecodeInt(value - operand.value, name);
		case "*":
			return new BytecodeInt(value * operand.value, name);
		case "/":
			return new BytecodeInt(value / operand.value, name);
		
		// TODO temporary line: to be rewritten
		default: throw new RuntimeException("Bad arithmetic operation " + op);
		}
	}
}
