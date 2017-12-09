package wyvern.tools.typedAST.interfaces;

import wyvern.tools.typedAST.core.expressions.Assignment;
import wyvern.tools.util.EvaluationEnvironment;

public interface Assignable extends ExpressionAST {
	@Deprecated
	default Value evaluateAssignment(Assignment ass, EvaluationEnvironment env) {
	    throw new RuntimeException("deprecated");
	}
}
