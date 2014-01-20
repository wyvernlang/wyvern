package wyvern.tools.lex;

import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.HasLocation;
import wyvern.tools.errors.ToolError;

public class MidLineState implements ILexerState {
	
	private MidLineState() {}
	private static MidLineState instance = new MidLineState();
	public static MidLineState getInstance() {
		return instance;
	}

	/** Implementation invariant: either returns a tail call to an
	 * ILexerState.getToken() implementation, or else calls
	 * lexData.setLexerState() just before returning a concrete token,
	 * or else returns EOF.
	 */
	@Override
	public Token getToken(ILexInput lexer, ILexData lexData) {
		
		// if end of file, return EOF token
		if (!lexer.hasNext()) {
			return InitialState.getInstance().getToken(lexer, lexData);
		}
		
		char ch = lexer.peek();
		
		// skip whitespace
		if (ch == ' ' || ch == '\t') {
			lexer.read();
			return getToken(lexer, lexData);
		}
		
		// on newline, go to initial state
		if (ch == '\n' || ch == '\r') {
			lexer.read();
			return InitialState.getInstance().getToken(lexer, lexData);
		}
		
		// read identifiers
		if (Character.isAlphabetic(ch)) {
			return IdentifierState.getInstance().getToken(lexer, lexData);
		}
		
		// read numbers
		if (Character.isDigit(ch)) {
			return NumberState.getInstance().getToken(lexer, lexData);
		}
		
		if (ch == '"') {
			lexer.read();
			return StringState.getInstance().getToken(lexer, lexData);
		}

        if (ch == '~') {
            lexer.read();
            lexData.setLexerState(this);
            return Token.getIdentifier("~");
        }
		
		if (LexUtils.isSymbol(ch)) {
			return SymbolState.getInstance().getToken(lexer, lexData);			
		}
		
		// else error
		ToolError.reportError(ErrorMessage.LEXER_ERROR, HasLocation.UNKNOWN);
		return null; // Unreachable.
	}

}
