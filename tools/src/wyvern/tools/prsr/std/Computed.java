package wyvern.tools.prsr.std;

import wyvern.tools.lex.ILexStream;
import wyvern.tools.prsr.Parser;
import wyvern.tools.prsr.ParserException;
import wyvern.tools.prsr.util.TransactionalStream;

import java.util.function.Function;

/**
 * Created by Ben Chung on 1/24/14.
 */
public class Computed<T,V> extends AbstractParser<T> {
	private final Parser<V> src;
	private final Function<V, T> tform;

	public Computed(Parser<V> src, Function<V,T> tform) {

		this.src = src;
		this.tform = tform;
	}

	@Override
	public T parse(ILexStream stream) throws ParserException {
		preParse();
		TransactionalStream ts = TransactionalStream.transaction(stream);
		try {
			V parse = src.parse(ts);
			T apply = tform.apply(parse);
			ts.commit();
			return apply;
		} catch (ParserException e) {
			ts.rollback();
			throw e;
		}
	}
}
