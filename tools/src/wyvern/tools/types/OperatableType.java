package wyvern.tools.types;

import wyvern.tools.typedAST.core.Invocation;

public interface OperatableType {
	Type checkOperator(Invocation opExp, Environment env);
}