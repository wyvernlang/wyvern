package wyvern.tools.typedAST;

import wyvern.tools.types.Environment;

public interface InvokableValue extends Value {
	Value evaluateInvocation(Invocation exp, Environment env);
}
