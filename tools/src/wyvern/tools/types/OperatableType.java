package wyvern.tools.types;

import wyvern.tools.typedAST.core.expressions.Invocation;

public interface OperatableType {
	Type checkOperator(Invocation opExp, Environment env);
}