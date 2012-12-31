package wyvern.tools.typedAST;

import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;

public abstract class CachingTypedAST extends AbstractTypedAST {
	private Type type;
	protected abstract Type doTypecheck(Environment env);
	
	@Override
	public final Type typecheck(Environment env) {
		type = doTypecheck(env);
		return type;
	}
	
	@Override
	public final Type getType() {
		if (type == null)
			throw new RuntimeException("called getType() before typechecking");
		return type;
	}
}
