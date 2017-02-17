package wyvern.tools.typedAST.core.binding.evaluation;

import wyvern.tools.typedAST.core.binding.NameBindingImpl;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.types.Type;
import wyvern.tools.util.EvaluationEnvironment;

public class ValueBinding extends NameBindingImpl implements EvaluationBinding {
	private Value value;
	
	public ValueBinding(String name, Value value) {
		super(name, (value != null)?value.getType():null);
		this.value = value;
	}

	public ValueBinding(String name, Type type) {
		super(name, type);
		this.value = null;	// to be set lazily
	}

	public TypedAST getUse() {
		return value;
	}
	
	public Value getValue(EvaluationEnvironment env) {
		return value;
	}
	
	public void setValue(Value newValue) {
		//assert value == null;
		//assert newValue != null;
		value = newValue;
	}
	
	@Override
	public String toString() {
		return "{" + getName() + " : " + getType() + " = " + getValue(null) + "}";
	}
}
