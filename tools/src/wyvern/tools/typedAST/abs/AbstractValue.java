package wyvern.tools.typedAST.abs;

import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;

import java.util.Optional;

public abstract class AbstractValue extends AbstractTypedAST implements Value {

	@Override
	public Type typecheck(Environment env, Optional<Type> expected) {
		return getType();
	}

	@Override
	public Value evaluate(Environment env) {
		return this;
	}
}
