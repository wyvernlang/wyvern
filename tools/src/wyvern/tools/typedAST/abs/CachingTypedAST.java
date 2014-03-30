package wyvern.tools.typedAST.abs;

import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;

import java.util.Optional;

public abstract class CachingTypedAST extends AbstractTypedAST {
	private Type type;
	protected abstract Type doTypecheck(Environment env, Optional<Type> expected);
	
	@Override
	public final Type typecheck(Environment env, Optional<Type> expected) {
		type = doTypecheck(env, expected);
		return type;
	}
	
	@Override
	public final Type getType() {
		if (type == null)
			throw new RuntimeException("called getType() before typechecking");
		return type;
	}
}
