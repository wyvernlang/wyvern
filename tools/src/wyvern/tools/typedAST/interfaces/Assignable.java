package wyvern.tools.typedAST.interfaces;

import wyvern.tools.typedAST.core.Assignment;
import wyvern.tools.types.Environment;

public interface Assignable {
	Value evaluateAssignment(Assignment ass, Environment env);
}
