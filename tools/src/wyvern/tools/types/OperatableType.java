package wyvern.tools.types;

import wyvern.tools.typedAST.Invocation;

public interface OperatableType {

	Type checkOperator(Invocation opExp, Environment env);

}
