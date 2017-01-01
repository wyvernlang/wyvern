package wyvern.tools.typedAST.abs;

import java.util.Optional;

import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.util.EvaluationEnvironment;

public abstract class AbstractValue extends AbstractExpressionAST implements Value {

	@Override
	public Type typecheck(Environment env, Optional<Type> expected) {
		return getType();
	}

	@Override
    @Deprecated
	public Value evaluate(EvaluationEnvironment env) {
		return this;
	}
}
