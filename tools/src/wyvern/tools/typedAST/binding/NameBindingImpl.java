package wyvern.tools.typedAST.binding;

import wyvern.tools.typedAST.TypedAST;
import wyvern.tools.typedAST.extensions.Variable;
import wyvern.tools.types.Type;

public class NameBindingImpl extends AbstractBinding implements NameBinding {
	public NameBindingImpl(String name, Type type) {
		super(name, type);
	}

	public TypedAST getUse() {
		return new Variable(this);
	}
}
