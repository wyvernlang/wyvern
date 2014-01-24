package wyvern.tools.prsr;

import wyvern.tools.lex.ILexStream;
import wyvern.tools.typedAST.interfaces.TypedAST;

/**
 * Created by Ben Chung on 1/24/14.
 */
public interface Parser<T> {
	public T parse(ILexStream stream) throws ParserException;
}
