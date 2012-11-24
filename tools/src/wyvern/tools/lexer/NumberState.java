package wyvern.tools.lexer;

public class NumberState implements LexerState {

	private NumberState() {}
	public static NumberState getInstance() {
		return new NumberState();
	}
	
	private StringBuffer buf = new StringBuffer();

	@Override
	public Token getToken(Lexer lexer) {
		if (lexer.hasNext()) {
			char ch = lexer.peek();
			
			// read number
			if (Character.isDigit(ch)) {
				
				lexer.read();
				buf.append(ch);
				return getToken(lexer);
			}
		}
		
		// if end of file or not an ID char, set next lexer state and return number read
		lexer.currentState = MidLineState.getInstance();
		return Token.getNumber(buf.toString(), lexer.getLineNumber());
	}

}
