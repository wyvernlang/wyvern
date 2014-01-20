package wyvern.tools.lex;

import java.io.InputStream;
import java.io.Reader;
import wyvern.tools.lex.Token.Kind;

public class LexStream implements ILexStream {
	private Token currentToken = null;
	private ILexInput lexInput;
	private ILexData lexData;

	public LexStream(String filename, Reader r) {
		lexInput = new LexInput(filename, r);
		lexData = new LexData();
	}
	
	@Override
	public Token next() {
		fill();
		Token token = currentToken;
		currentToken = null;
		return token;
	}

	@Override
	public Token peek() {
		fill();
		return currentToken;
	}

	@Override
	public ILexStream dslBlock(Kind start, Kind end) {
		// TODO implement me
		throw new UnsupportedOperationException();
	}

	@Override
	public Token lookAhead(int n) {
		// TODO implement me (probably later)
		throw new UnsupportedOperationException();
	}

	@Override
	public InputStream asRawStream() {
		// TODO implement me (probably later)
		throw new UnsupportedOperationException();
	}
	
	/** ensures currentTok != null */
	private void fill() {
		if (currentToken == null) {
			currentToken = lexData.getLexerState().getToken(lexInput, lexData);
		}		
	}	
}
