package wyvern.tools.typedAST.core.binding.typechecking;

import wyvern.tools.typedAST.core.binding.LateBinder;
import wyvern.tools.types.Type;

/**
 * Created by Ben Chung on 12/1/13.
 */
public class LateTypeBinding extends TypeBinding {

	private LateBinder<Type> type;

	public LateTypeBinding(String name, LateBinder<Type> type) {
		super(name, null);
		this.type = type;
	}

	@Override
	public Type getType() {
		return type.get();
	}
}
