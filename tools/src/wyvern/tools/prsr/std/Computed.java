package wyvern.tools.prsr.std;

import wyvern.tools.lex.ILexStream;
import wyvern.tools.prsr.Parser;
import wyvern.tools.prsr.ParserException;
import wyvern.tools.prsr.util.TransactionalStream;

import java.util.function.Function;

/**
 * Created by Ben Chung on 1/24/14.
 */
public class Computed<T,V> implements Parser<T> {
	private final Parser<V> src;
	private final Function<V, T> tform;

	public Computed(Parser<V> src, Function<V,T> tform) {

		this.src = src;
		this.tform = tform;
	}

	@Override
	public T parse(ILexStream stream) throws ParserException {
		try {
			TransactionalStream ts = new TransactionalStream(stream);
			T apply = tform.apply(src.parse(ts));
			ts.apply();
			return apply;
		} catch (ParserException e) {
			throw e;
		}
	}
}
