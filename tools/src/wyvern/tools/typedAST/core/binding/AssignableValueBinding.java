package wyvern.tools.typedAST.core.binding;

import wyvern.tools.typedAST.core.binding.evaluation.ValueBinding;
import wyvern.tools.typedAST.interfaces.Value;

public abstract class AssignableValueBinding extends ValueBinding {
	public AssignableValueBinding(String name, Value value) { super(name, value);}

	public abstract void assign(Value value);
}
