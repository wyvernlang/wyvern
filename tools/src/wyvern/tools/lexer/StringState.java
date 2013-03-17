package wyvern.tools.lexer;

import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.HasLocation;
import wyvern.tools.errors.ToolError;

public class StringState implements LexerState {

	private StringState() {}
	public static StringState getInstance() {
		return new StringState();
	}

	StringBuffer buf = new StringBuffer();
	
	@Override
	public Token getToken(Lexer lexer) {
		//Is it a string?
		if (lexer.hasNext()) {
			char ch = lexer.read();
			//Has the string ended?
			if (ch == '"') {
				lexer.currentState = MidLineState.getInstance();
				return Token.getString(buf.toString(), lexer.getLocation());
			}
			buf.append(ch);
			return getToken(lexer);
		}
		
		//Unterminated string
		ToolError.reportError(ErrorMessage.LEXER_ERROR, HasLocation.UNKNOWN);
		return null; // Unreachable.
	}

}
