package wyvern.tools.typedAST;

import wyvern.tools.types.Environment;

public interface ApplyableValue extends Value {
	Value evaluateApplication(Application app, Environment env);
}
