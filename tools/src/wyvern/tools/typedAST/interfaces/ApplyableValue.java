package wyvern.tools.typedAST.interfaces;

import wyvern.tools.typedAST.core.expressions.Application;
import wyvern.tools.types.Environment;
import wyvern.tools.util.EvaluationEnvironment;

public interface ApplyableValue extends Value {
	Value evaluateApplication(Application app, EvaluationEnvironment env);
}
