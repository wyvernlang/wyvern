package wyvern.tools.typedAST.interfaces;

import wyvern.tools.typedAST.core.expressions.Invocation;
import wyvern.tools.types.Environment;

public interface InvokableValue extends Value {
	Value evaluateInvocation(Invocation exp, Environment env);
}
