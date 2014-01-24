package wyvern.tools.prsr.std;

import wyvern.tools.lex.ILexStream;
import wyvern.tools.prsr.Parser;
import wyvern.tools.prsr.ParserException;
import wyvern.tools.util.Reference;

/**
 * Created by Ben Chung on 1/24/14.
 */
public class Ref<T> implements Parser<T> {
	private Reference<Parser<T>> ref;

	public Ref(Reference<Parser<T>> ref) {
		this.ref = ref;
	}

	@Override
	public T parse(ILexStream stream) throws ParserException {
		return ref.get().parse(stream);
	}
}
