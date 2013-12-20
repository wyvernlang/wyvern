package wyvern.tools.bytecode.values;

public class BytecodeInt extends AbstractBytecodeValue<Integer> {
	
	public BytecodeInt(int v, String n) {
		super(v,n);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof BytecodeInt)) {
			return false;
		}
		BytecodeInt bci = (BytecodeInt) obj;
		return name.equals(bci.getName());
	}
}
