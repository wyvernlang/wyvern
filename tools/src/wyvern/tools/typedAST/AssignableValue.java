package wyvern.tools.typedAST;

import wyvern.tools.types.Environment;

public interface AssignableValue {
	Value evaluateAssignment(Assignment ass, Environment env);
}
