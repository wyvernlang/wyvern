package wyvern.tools.typedAST.binding;

import wyvern.tools.typedAST.TypedAST;
import wyvern.tools.typedAST.Value;
import wyvern.tools.util.TreeWriter;

public class ValueBinding extends NameBindingImpl {
	private Value value;
	
	public ValueBinding(String name, Value value) {
		super(name, value.getType());
		this.value = value;
	}

	public TypedAST getUse() {
		return value;
	}
	
	public Value getValue() {
		return value;
	}
	@Override
	public void writeArgsToTree(TreeWriter writer) {
		super.writeArgsToTree(writer);
		writer.writeArgs(value);
	}
}
