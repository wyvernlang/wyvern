package wyvern.target.corewyvernIL;

import wyvern.target.corewyvernIL.type.ValueType;

public class FormalArg extends ASTNode {

	private String name;
	private ValueType type;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public ValueType getType() {
		return type;
	}
	
	public void setType(ValueType type) {
		this.type = type;
	}
}
