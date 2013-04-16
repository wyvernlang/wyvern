package wyvern.tools.types;

import wyvern.tools.typedAST.core.Assignment;

public interface AssignableType {
	Type checkAssignment(Assignment application, Environment env);
}