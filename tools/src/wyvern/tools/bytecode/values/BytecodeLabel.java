package wyvern.tools.bytecode.values;

public class BytecodeLabel extends AbstractBytecodeValue<Integer> {

	private final int labelID;
	
	// first integer is the pc value it points to, second is its label number
	public BytecodeLabel(Integer v, int i) {
		super(v);
		labelID = i;
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof BytecodeLabel)) {
			return false;
		}
		BytecodeLabel blv = (BytecodeLabel) obj;
		return labelID == blv.labelID;
	}
	
	public int getLabelID() {
		return labelID;
	}

	@Override
	public BytecodeValue doInvoke(BytecodeValue operand, String op) {
		throw new RuntimeException("trying to do math on a label");
	}

}
