package wyvern.tools.typedAST.binding;

import wyvern.tools.types.Type;
import wyvern.tools.util.TreeWriter;

public abstract class AbstractBinding implements Binding {
	private String name;
	private Type type;
	
	public AbstractBinding(String name, Type type) {
		this.name = name;
		this.type = type;
	}
	
	public String getName() {
		return name;
	}
	
	public Type getType() {
		return type;
	}
	
	@Override
	public void writeArgsToTree(TreeWriter writer) {
		writer.writeArgs(name, type);
	}
}