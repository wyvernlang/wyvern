package wyvern.tools.lexer;

public class IdentifierState implements LexerState {

	private IdentifierState() {}
	public static IdentifierState getInstance() {
		return new IdentifierState();
	}
	
	private StringBuffer buf = new StringBuffer();

	@Override
	public Token getToken(Lexer lexer) {
		if (lexer.hasNext()) {
			char ch = lexer.peek();
			
			// read identifiers
			if (Character.isJavaIdentifierPart(ch)) {
				lexer.read();
				buf.append(ch);
				return getToken(lexer);
			}
		}
		
		// if end of file or not an ID char, set next lexer state and return identifier so far
		lexer.currentState = MidLineState.getInstance();
		return Token.getIdentifier(buf.toString(), lexer.getLocation());
	}

}
