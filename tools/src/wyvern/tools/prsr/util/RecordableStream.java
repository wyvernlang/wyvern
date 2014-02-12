package wyvern.tools.prsr.util;

import wyvern.tools.lex.ILexStream;
import wyvern.tools.lex.Token;

import java.io.InputStream;
import java.util.function.Consumer;

/**
 * Created by Ben Chung on 2/9/14.
 */
public class RecordableStream implements ILexStream {
	private final ILexStream inner;
	private int delta = 0;

	private RecordableStream(ILexStream observe) {
		this.inner = observe;
	}

	private Consumer<ILexStream> getDelta() {
		return str -> {
			for (int i = 0; i < delta; i++)
				str.next();
		};
	}

	public static ILexStream start(ILexStream observe) {
		if (observe instanceof TransactionalStream)
			return observe;
		else
			return new RecordableStream(observe);
	}

	public static Consumer<ILexStream> stop(ILexStream observed) {
		if (observed instanceof TransactionalStream)
			return ((TransactionalStream) observed).getDelta();
		else if (observed instanceof RecordableStream)
			return ((RecordableStream) observed).getDelta();
		throw new RuntimeException();
	}

	@Override
	public Token next() {
		delta++;
		return inner.next();
	}

	@Override
	public Token peek() {
		return inner.peek();
	}

	@Override
	public Token lookAhead(int n) {
		return inner.lookAhead(n);
	}

	@Override
	public ILexStream dslBlock(Token.Kind start, Token.Kind end) {
		return null;
	}

	@Override
	public InputStream asRawStream() {
		return null;
	}
}
