package wyvern.tools.typedAST.core.binding.typechecking;

import wyvern.tools.typedAST.core.binding.NameBindingImpl;
import wyvern.tools.types.Type;

public class AssignableNameBinding extends NameBindingImpl {
	public AssignableNameBinding(String name, Type type) {
		super(name, type);
	}
}