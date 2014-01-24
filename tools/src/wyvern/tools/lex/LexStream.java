package wyvern.tools.lex;

import java.io.InputStream;
import java.io.Reader;
import java.util.LinkedList;
import java.util.Queue;

import wyvern.tools.lex.Token.Kind;

/** Current limitations:
 *   - DSL blocks are not implemented (and thus neither are raw streams)
 *   - lookahead is not implemented
 *   - the lexer still generates INDENT and DEDENT between parentheses
 * 
 * @author aldrich
 *
 */
public class LexStream implements ILexStream {
	private Token currentToken = null;
	private ILexInput lexInput;
	private ILexData lexData;

	private LinkedList<Token> lookaheadCache = new LinkedList<>();

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
		if (lookaheadCache.size() > n) {
			return lookaheadCache.get(n);
		}
		LinkedList<Token> tokens = new LinkedList<>();
		for (int i = 0; i < n + 1; i++) {
			tokens.add(next());
		}
		Token result = tokens.getLast();
		tokens.addAll(lookaheadCache);
		lookaheadCache = tokens;
		return result;
	}

	@Override
	public InputStream asRawStream() {
		// TODO implement me (probably later)
		throw new UnsupportedOperationException();
	}
	
	/** ensures currentTok != null */
	private void fill() {
		if (currentToken == null) {
			if (lookaheadCache.size() > 0){
				currentToken = lookaheadCache.poll();
				return;
			}
			currentToken = lexData.getLexerState().getToken(lexInput, lexData);
		}		
	}	
}
