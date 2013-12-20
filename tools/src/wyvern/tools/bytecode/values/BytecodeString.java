package wyvern.tools.bytecode.values;

public class BytecodeString extends AbstractBytecodeValue<String> {

	public BytecodeString(String v, String n) {
		super(v,n);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof BytecodeString)) {
			return false;
		}
		BytecodeString bci = (BytecodeString) obj;
		return name.equals(bci.getName());
	}
}
