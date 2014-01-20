package wyvern.tools.lex;

public class NumberState implements ILexerState {

	private NumberState() {}
	private final static NumberState instance = new NumberState();
	public static NumberState getInstance() {
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
			if (lexer.hasNext()) {
				char ch = lexer.peek();
				
				// read number
				if (Character.isDigit(ch)) {
					
					lexer.read();
					buf.append(ch);
					continue;
				}
			}
			
			// if end of file or not an ID char, set next lexer state and return number read
			lexData.setLexerState(MidLineState.getInstance());
			return Token.getNumber(buf.toString(), lexer.getLocation());
		}
	}

}
