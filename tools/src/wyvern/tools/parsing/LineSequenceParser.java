package wyvern.tools.parsing;

import wyvern.tools.rawAST.LineSequence;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.types.Environment;

public interface LineSequenceParser {

	// POSTCONDITION: has parsed everything in rest
	TypedAST parse(TypedAST first, LineSequence rest, Environment env);
}
