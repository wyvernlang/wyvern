package wyvern.tools.parsing;

import wyvern.tools.rawAST.LineSequence;
import wyvern.tools.typedAST.TypedAST;
import wyvern.tools.types.Environment;

public interface LineSequenceParser {
	TypedAST parse(TypedAST first, LineSequence rest, Environment env);
}