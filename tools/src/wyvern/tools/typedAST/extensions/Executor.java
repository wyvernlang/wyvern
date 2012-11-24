package wyvern.tools.typedAST.extensions;

import wyvern.tools.typedAST.Value;

public interface Executor {
	Value execute(Value argument);
}
