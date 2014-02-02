package wyvern.tools.prsr.std;

import wyvern.tools.lex.ILexStream;
import wyvern.tools.prsr.Parser;
import wyvern.tools.prsr.ParserException;
import wyvern.tools.prsr.util.TransactionalStream;
import wyvern.tools.util.Pair;

/**
 * Created by Ben Chung on 1/24/14.
 */
public class Concat<T,V> extends AbstractParser<Pair<T,V>> {
	public static <L,K> AbstractParser<K> getRightConcat(Parser<L> first, Parser<K> last) {
		return new Computed<>(new Concat<L,K>(first, last), (tuple) -> tuple.second);
	}
	public static <L,K> AbstractParser<L> getLeftConcat(Parser<L> first, Parser<K> last) {
		return new Computed<>(new Concat<L,K>(first, last), (tuple) -> tuple.first);
	}

	private final Parser<T> first;
	private final Parser<V> last;

	public Concat(Parser<T> first, Parser<V> last) {

		this.first = first;
		this.last = last;
	}

	@Override
	public Pair<T,V> parse(ILexStream stream) throws ParserException {
		preParse();
		TransactionalStream ts = TransactionalStream.transaction(stream);
		T a;
		V b;
		try {
			a = first.parse(ts);
			b = last.parse(ts);
			ts.commit();
		} catch (ParserException e) {
			ts.rollback();
			throw e;
		}
		return new Pair<>(a,b);
	}
}
