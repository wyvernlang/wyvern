package wyvern.tools.typedAST.interfaces;

import wyvern.tools.typedAST.core.expressions.Assignment;
import wyvern.tools.types.Environment;
import wyvern.tools.util.EvaluationEnvironment;

public interface Assignable extends ExpressionAST {
	void checkAssignment(Assignment ass, Environment env);
	Value evaluateAssignment(Assignment ass, EvaluationEnvironment env);
}
