package wyvern.tools.lexer;

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
				return Token.getString(buf.toString(), lexer.getLineNumber());
			}
			buf.append(ch);
			return getToken(lexer);
		}
		
		//Unterminated string
		throw new LexerException();
	}

}
