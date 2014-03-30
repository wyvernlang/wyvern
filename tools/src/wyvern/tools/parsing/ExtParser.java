package wyvern.tools.parsing;

import wyvern.tools.typedAST.interfaces.TypedAST;

public interface ExtParser {
	public TypedAST parse(String input);
}
