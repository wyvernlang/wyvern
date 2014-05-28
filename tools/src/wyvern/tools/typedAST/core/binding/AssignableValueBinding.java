package wyvern.tools.typedAST.core.binding;

import wyvern.tools.typedAST.interfaces.Value;

public interface AssignableValueBinding extends NameBinding {
	public void assign(Value value);
}
