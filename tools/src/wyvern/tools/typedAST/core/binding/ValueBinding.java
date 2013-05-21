package wyvern.tools.typedAST.core.binding;

import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.util.TreeWriter;

public class ValueBinding extends NameBindingImpl {
	private Value value;
	
	public ValueBinding(String name, Value value) {
		super(name, value.getType());
		this.value = value;
	}

	public ValueBinding(String name, Type type) {
		super(name, type);
		this.value = null;	// to be set lazily
	}

	public TypedAST getUse() {
		return value;
	}
	
	public Value getValue(Environment env) {
		return value;
	}
	
	public void setValue(Value newValue) {
		//assert value == null;
		//assert newValue != null;
		value = newValue;
	}
	
	@Override
	public void writeArgsToTree(TreeWriter writer) {
		super.writeArgsToTree(writer);
		writer.writeArgs(value);
	}
}