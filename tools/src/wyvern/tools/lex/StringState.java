package wyvern.tools.lex;

import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.HasLocation;
import wyvern.tools.errors.ToolError;

public class StringState implements ILexerState {

	private StringState() {}
	private final static StringState instance = new StringState();
	public static StringState getInstance() {
		return instance;
	}

	
	/** Implementation invariant: either returns a tail call to an
	 * ILexerState.getToken() implementation, or else calls
	 * lexData.setLexerState() just before returning a concrete token,
	 * or else returns EOF.
	 */
	@Override
	public Token getToken(ILexInput lexer, ILexData lexData) {
		StringBuffer buf = new StringBuffer();
		while (true) {
			//Is it a string?
			if (lexer.hasNext()) {
				char ch = lexer.read();
				//Has the string ended?
				if (ch == '"') {
					lexData.setLexerState(MidLineState.getInstance());
					return Token.getString(buf.toString(), lexer.getLocation());
				}
				buf.append(ch);
				continue;
			}
			
			//Unterminated string
			ToolError.reportError(ErrorMessage.LEXER_ERROR, HasLocation.UNKNOWN);
		}
	}

}
