package wyvern.tools.typedAST.interfaces;

import wyvern.tools.typedAST.core.expressions.Invocation;
import wyvern.tools.util.EvaluationEnvironment;

public interface InvokableValue extends Value {
	default Value evaluateInvocation(Invocation exp, EvaluationEnvironment env) {
        throw new RuntimeException();
    }
}
