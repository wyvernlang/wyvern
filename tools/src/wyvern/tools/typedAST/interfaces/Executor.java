package wyvern.tools.typedAST.interfaces;


import wyvern.tools.types.Environment;

public interface Executor {
	Value execute(Environment execEnv, Value argument);
}
