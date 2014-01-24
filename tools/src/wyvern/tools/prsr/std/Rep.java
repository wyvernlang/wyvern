package wyvern.tools.prsr.std;

import wyvern.tools.lex.ILexStream;
import wyvern.tools.prsr.Parser;
import wyvern.tools.prsr.ParserException;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Ben Chung on 1/24/14.
 */
public class Rep<T> implements Parser<List<T>> {
	private Parser<T> rep;

	public Rep(Parser<T> rep) {
		this.rep = rep;
	}
	@Override
	public List<T> parse(ILexStream stream) throws ParserException {
		LinkedList<T> output = new LinkedList<>();
		while (true) {
			try {
				TransactionalStream ts = new TransactionalStream(stream);
				output.add(rep.parse(ts));
				ts.apply();
			} catch (ParserException e) {
				break;
			}
		}
		return output;
	}
}
