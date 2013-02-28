package wyvern.tools.typedAST;

import wyvern.tools.types.Environment;

public interface Assignable {
	Value evaluateAssignment(Assignment ass, Environment env);
}
