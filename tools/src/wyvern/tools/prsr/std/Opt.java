package wyvern.tools.prsr.std;

import wyvern.tools.lex.ILexStream;
import wyvern.tools.prsr.Parser;
import wyvern.tools.prsr.ParserException;

/**
 * Created by Ben Chung on 1/24/14.
 */
public class Opt<T> implements Parser<T> {
	private Parser<T> inner;

	public Opt(Parser<T> inner) {
		this.inner = inner;
	}

	@Override
	public T parse(ILexStream stream) throws ParserException {
		try {
			return inner.parse(stream);
		} catch (ParserException e) {
			return null;
		}
	}
}
