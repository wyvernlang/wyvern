package wyvern.tools.prsr.std;

import wyvern.tools.lex.ILexStream;
import wyvern.tools.prsr.Parser;
import wyvern.tools.prsr.ParserException;
import wyvern.tools.prsr.util.TransactionalStream;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Ben Chung on 1/24/14.
 */
public class Rep<T> extends AbstractParser<List<T>> {
	private Parser<T> rep;

	public Rep(Parser<T> rep) {
		this.rep = rep;
	}
	@Override
	public List<T> parse(ILexStream stream) throws ParserException {
		preParse();
		LinkedList<T> output = new LinkedList<>();
		while (true) {
			TransactionalStream ts = TransactionalStream.transaction(stream);
			try {
				output.add(rep.parse(ts));
				ts.commit();
			} catch (ParserException e) {
				ts.rollback();
				break;
			}
		}
		return output;
	}
}
