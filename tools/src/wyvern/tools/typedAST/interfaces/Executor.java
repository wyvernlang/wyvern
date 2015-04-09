package wyvern.tools.typedAST.interfaces;


import wyvern.tools.types.Environment;
import wyvern.tools.util.EvaluationEnvironment;

public interface Executor {
	Value execute(EvaluationEnvironment execEnv, Value argument);
}
