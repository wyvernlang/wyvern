package wyvern.tools.prsr;

import wyvern.tools.lex.ILexStream;
import wyvern.tools.typedAST.interfaces.TypedAST;

import java.util.function.Function;

public interface ParserStage {
	TypedAST parse(ILexStream stream, ParserStage self, ParseHandle next);
}
