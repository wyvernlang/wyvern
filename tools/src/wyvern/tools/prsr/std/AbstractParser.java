package wyvern.tools.prsr.std;

import wyvern.tools.lex.ILexStream;
import wyvern.tools.lex.Token;
import wyvern.tools.prsr.Parser;
import wyvern.tools.prsr.ParserException;
import wyvern.tools.util.Pair;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

/**
 * Created by Ben Chung on 1/26/14.
 */
public abstract class AbstractParser<T> implements Parser<T> {
	private boolean shouldPause = false;
	public AbstractParser<T> toggleBreak() {
		shouldPause = !shouldPause;
		return this;
	}

	protected void preParse() {
		if (shouldPause)
			return;
	}

	public static<T> Parser<T> getParser(Function<ILexStream, T> inp) {
		return new AbstractParser<T>() {
			@Override
			public T parse(ILexStream stream) throws ParserException {
				return inp.apply(stream);
			}
		};
	}

	public <V> AbstractParser<Pair<T, V>> concat(Parser<V> right) {
		return new Concat<>(this, right);
	}

	public <V> AbstractParser<V> concatRight(Parser<V> right) {
		return Concat.getRightConcat(this, right);
	}

	public <V> AbstractParser<T> concatLeft(Parser<V> right) {
		return Concat.getLeftConcat(this, right);
	}

	public <V> AbstractParser<V> compute(Function<T,V> tform) {
		return new Computed<>(this, tform);
	}

	public AbstractParser<T> opt() {
		return new Opt<>(this);
	}

	public AbstractParser<List<T>> rep() {
		return new Rep<>(this);
	}
	public Parser<List<T>> rep1() {
		return this.concat(this.rep()).compute(pair -> {LinkedList<T> out = new LinkedList<>(); out.add(pair.first); out.addAll(pair.second); return out;});
	}
}
