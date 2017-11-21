package wyvern.tools.typedAST.interfaces;

import wyvern.tools.typedAST.core.expressions.Assignment;
import wyvern.tools.types.Environment;
import wyvern.tools.util.EvaluationEnvironment;

public interface Assignable extends ExpressionAST {
    @Deprecated
	default void checkAssignment(Assignment ass, Environment env) {
        throw new RuntimeException("deprecated");
    }
	@Deprecated
	default Value evaluateAssignment(Assignment ass, EvaluationEnvironment env) {
	    throw new RuntimeException("deprecated");
	}
}
