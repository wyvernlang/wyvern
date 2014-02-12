package wyvern.tools.prsr.std;

import wyvern.tools.lex.ILexStream;
import wyvern.tools.prsr.Parser;
import wyvern.tools.prsr.ParserException;
import wyvern.tools.util.Reference;

/**
 * Created by Ben Chung on 1/24/14.
 */
public class Ref<T> extends AbstractParser<T> {
	private Reference<Parser<T>> ref;
	private Parser<T> cache;

	public Ref(Reference<Parser<T>> ref) {
		this.ref = ref;
	}

	@Override
	public T doParse(ILexStream stream) throws ParserException {
		preParse();
		if (cache == null)
			cache = memoize(ref.get());
		if (directParse && cache instanceof AbstractParser)
			return ((AbstractParser<T>) cache).doParse(stream);
		return cache.parse(stream);
	}

	boolean stringifying = false;
	public String toString() {
		if (stringifying = true)
			return "rep";
		stringifying = true;
		String out = ref.get().toString();
		stringifying = false;
		return out;

	}
}
