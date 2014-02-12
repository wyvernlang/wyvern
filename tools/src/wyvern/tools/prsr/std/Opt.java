package wyvern.tools.prsr.std;

import wyvern.tools.lex.ILexStream;
import wyvern.tools.prsr.Parser;
import wyvern.tools.prsr.ParserException;

import java.util.Optional;


/**
 * Created by Ben Chung on 1/24/14.
 */
public class Opt<T> extends AbstractParser<Optional<T>> {
	private Parser<T> inner;

	public Opt(Parser<T> inner) {
		this.inner = inner;
	}

	@Override
	public Optional<T> doParse(ILexStream stream) throws ParserException {
		preParse();
		try {
			return Optional.of(memoize(inner).parse(stream));
		} catch (ParserException e) {
			return Optional.empty();
		}
	}

	public String toString() {
		return inner + ".?";
	}
}
