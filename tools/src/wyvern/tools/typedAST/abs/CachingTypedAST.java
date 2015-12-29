package wyvern.tools.typedAST.abs;

import wyvern.tools.typedAST.interfaces.ExpressionAST;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;

import java.util.Map;
import java.util.Optional;

public abstract class CachingTypedAST extends AbstractExpressionAST implements ExpressionAST {
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


	protected abstract ExpressionAST doClone(Map<String, TypedAST> nc);
	@Override
	public ExpressionAST cloneWithChildren(Map<String, TypedAST> nc) {
		ExpressionAST res = doClone(nc);
		if (res instanceof CachingTypedAST)
			((CachingTypedAST) res).type = type;
		return res;
	}
}
