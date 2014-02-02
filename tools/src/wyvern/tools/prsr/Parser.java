package wyvern.tools.prsr;

import wyvern.tools.lex.ILexStream;
import wyvern.tools.prsr.std.Concat;
import wyvern.tools.prsr.std.Opt;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.util.Pair;

import java.util.List;
import java.util.function.Function;

/**
 * Created by Ben Chung on 1/24/14.
 */
public interface Parser<T> {
	public T parse(ILexStream stream) throws ParserException;

	public <V> Parser<Pair<T, V>> concat(Parser<V> right);

	public <V> Parser<V> concatRight(Parser<V> right);

	public <V> Parser<T> concatLeft(Parser<V> right);

	public <V> Parser<V> compute(Function<T,V> tform);

	public Parser<T> opt();

	public Parser<List<T>> rep();

	public Parser<List<T>> rep1();
}
