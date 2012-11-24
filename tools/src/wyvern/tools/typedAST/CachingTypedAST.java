package wyvern.tools.typedAST;

import wyvern.tools.types.Type;

public abstract class CachingTypedAST extends AbstractTypedAST {
	private Type type;
	protected abstract Type doTypecheck();
	
	@Override
	public final Type typecheck() {
		type = doTypecheck();
		return type;
	}
	
	@Override
	public final Type getType() {
		if (type == null)
			throw new RuntimeException("called getType() before typechecking");
		return type;
	}
}
