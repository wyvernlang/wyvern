package wyvern.tools.prsr.std;

import wyvern.tools.lex.ILexStream;
import wyvern.tools.prsr.Parser;
import wyvern.tools.prsr.ParserException;
import wyvern.tools.prsr.util.TransactionalStream;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Ben Chung on 1/24/14.
 */
public class Or<T> implements Parser<T> {
	private List<Parser<T>> clauses;

	public Or(List<Parser<T>> clauses) {
		this.clauses = clauses;
	}

	public Or(Parser<T>... parsers) {
		this.clauses = Arrays.asList(parsers);
	}

	@Override
	public T parse(ILexStream stream) throws ParserException {
		for (Parser<T> parser : clauses)
			try {
				TransactionalStream ts = new TransactionalStream(stream);
				T result = parser.parse(ts);
				ts.apply();
				return result;
			} catch (ParserException e) {

			}
		throw new ParserException();
	}
}
