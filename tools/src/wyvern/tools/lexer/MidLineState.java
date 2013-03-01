package wyvern.tools.lexer;

import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.HasLocation;
import wyvern.tools.errors.ToolError;

public class MidLineState implements LexerState {
	
	private MidLineState() {}
	private static MidLineState instance = new MidLineState();
	public static MidLineState getInstance() {
		return instance;
	}

	@Override
	public Token getToken(Lexer lexer) {
		
		// if end of file, return EOF token
		if (!lexer.hasNext()) {
			return InitialState.getInstance().getToken(lexer);
		}
		
		char ch = lexer.peek();
		
		// skip whitespace
		if (ch == ' ' || ch == '\t') {
			lexer.read();
			return getToken(lexer);
		}
		
		// on newline, go to initial state
		if (ch == '\n' || ch == '\r') {
			lexer.read();
			return InitialState.getInstance().getToken(lexer);
		}
		
		// read identifiers
		if (Character.isAlphabetic(ch)) {
			return IdentifierState.getInstance().getToken(lexer);
		}
		
		// read numbers
		if (Character.isDigit(ch)) {
			return NumberState.getInstance().getToken(lexer);
		}
		
		if (ch == '"') {
			lexer.read();
			return StringState.getInstance().getToken(lexer);
		}
		
		if (lexer.isSymbol(ch)) {
			return SymbolState.getInstance().getToken(lexer);			
		}
		
		// else error
		ToolError.reportError(ErrorMessage.LEXER_ERROR, HasLocation.UNKNOWN);
		return null; // Unreachable.
	}

}
