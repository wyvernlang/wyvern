package wyvern.target.corewyvernIL.binding;

import wyvern.target.corewyvernIL.type.Type;

public abstract class Binding {
	private String name;
	private Type type;
	
	public Binding(String name, Type type) {
		this.name = name;
		this.type = type;
	}
	
	public String getName() {
		return name;
	}
	
	public Type getType() {
		return type;
	}
}
