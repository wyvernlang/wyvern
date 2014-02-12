package wyvern.tools.prsr.std;

import wyvern.tools.errors.FileLocation;
import wyvern.tools.lex.ILexStream;
import wyvern.tools.prsr.Parser;
import wyvern.tools.prsr.ParserException;
import wyvern.tools.prsr.util.RecordableStream;
import wyvern.tools.util.Pair;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created by Ben Chung on 1/26/14.
 */
public abstract class AbstractParser<T> implements Parser<T> {
	private Map<Pair<Parser<?>, FileLocation>, Pair<Consumer<ILexStream>, Object>> memo;

	private static class ParseFailure {
		private ParserException inner;

		public ParseFailure(ParserException inner) {

			this.inner = inner;
		}

		public ParserException getInner() {
			return inner;
		}
	}

	public AbstractParser() {
		this(null);
	}

	public AbstractParser(Map<Pair<Parser<?>, FileLocation>, Pair<Consumer<ILexStream>, Object>> memo) {
		this.memo = memo;
	}

	protected boolean directParse = true;
	private boolean shouldPause = false;
	public AbstractParser<T> toggleBreak() {
		shouldPause = !shouldPause;
		return this;
	}

	protected void preParse() {
		if (shouldPause)
			return;
	}

	public final T parse(ILexStream stream) throws ParserException {
		directParse = false;
		FileLocation location = stream.peek().location;
		Pair<Parser<?>, FileLocation> key = new Pair<>(this, location);

		if (memo == null)
			memo = new HashMap<>();

		if (false && memo.containsKey(key)) {
			if (memo.get(key).first instanceof ParseFailure)
				throw ((ParseFailure) memo.get(key).first).inner; // The stream can't move for a failure
			//The parser is equal and in the same position. Context-free => same object, safe cast.
			Pair<Consumer<ILexStream>, Object> memoed = memo.get(key);
			memoed.first.accept(stream);
			return (T) memoed.second;
		}

		try {
			ILexStream irs = RecordableStream.start(stream);
			T parse = doParse(irs);
			//memo.put(key, new Pair<Consumer<ILexStream>, Object>(RecordableStream.stop(irs), parse));
			return parse;
		} catch (ParserException e) {
			//memo.put(key, new Pair<>(str -> {}, new ParseFailure(e)));
			throw e;
		}
	}
	protected abstract T doParse(ILexStream stream) throws ParserException;

	protected <V> Parser<V> memoize(Parser<V> newParser) {
		//if (newParser instanceof AbstractParser)
		//	((AbstractParser) newParser).memo = this.memo;
		return newParser;
	}

	public static<T> Parser<T> getParserOpt(Function<ILexStream, Optional<T>> inp) {
		return new AbstractParser<T>() {
			@Override
			public T doParse(ILexStream stream) throws ParserException {
				return inp.apply(stream).orElseThrow(() -> new ParserException(stream.peek()));
			}
		};
	}

	public static<T> Parser<T> getParser(Function<ILexStream, T> inp) {
		return new AbstractParser<T>() {
			@Override
			public T doParse(ILexStream stream) throws ParserException {
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

	public AbstractParser<Optional<T>> opt() {
		return new Opt<T>(this);
	}

	public AbstractParser<List<T>> rep() {
		return new Rep<>(this);
	}
	public Parser<List<T>> rep1() {
		return this.concat(this.rep()).compute(pair -> {
			LinkedList<T> out = new LinkedList<>();
			out.add(pair.first);
			out.addAll(pair.second);
			return out;
		});
	}
}
