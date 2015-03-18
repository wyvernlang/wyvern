package wyvern.tools.typedAST.core.binding.typechecking;

import wyvern.tools.typedAST.core.binding.LateBinder;
import wyvern.tools.typedAST.core.binding.NameBindingImpl;
import wyvern.tools.types.Type;

/**
 * Created by Ben Chung on 12/1/13.
 */
public class LateNameBinding extends NameBindingImpl {

	private LateBinder<Type> type;

	public LateNameBinding(String name, LateBinder<Type> type) {
		super(name, null);
		this.type = type;
	}

	@Override
	public Type getType() {
		return type.get();
	}

	@Override
	public String toString() {
		return "LateNameBinding("+getName()+")";
	}
}
