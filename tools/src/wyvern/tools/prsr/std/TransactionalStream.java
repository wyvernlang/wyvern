package wyvern.tools.prsr.std;

import wyvern.tools.lex.ILexStream;
import wyvern.tools.lex.Token;

import java.io.InputStream;

/**
 * Created by Ben Chung on 1/24/14.
 */
public class TransactionalStream implements ILexStream {
	private ILexStream source;
	private int ptr = 0;

	public TransactionalStream(ILexStream source) {

		this.source = source;
	}

	@Override
	public Token next() {
		return source.lookAhead(ptr++);
	}

	@Override
	public Token peek() {
		return source.lookAhead(ptr);
	}

	@Override
	public Token lookAhead(int n) {
		return source.lookAhead(ptr + n);
	}

	@Override
	public ILexStream dslBlock(Token.Kind start, Token.Kind end) {
		return source.dslBlock(start, end);
	}

	@Override
	public InputStream asRawStream() {
		return source.asRawStream();
	}

	public void apply() {
		for (; ptr > 0; ptr--) {
			source.next();
		}
	}
}
