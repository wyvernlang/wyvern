package wyvern.tools.lex;

public class IdentifierState implements ILexerState {

	private IdentifierState() {}
	private final static IdentifierState instance = new IdentifierState();
	public static IdentifierState getInstance() {
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
				
				// read identifiers
				if (Character.isJavaIdentifierPart(ch)) {
					lexer.read();
					buf.append(ch);
					continue;
				}
			}
			
			// if end of file or not an ID char, set next lexer state and return identifier so far
			lexData.setLexerState(MidLineState.getInstance());
			return Token.getIdentifier(buf.toString(), lexer.getLocation());
		}
	}

}
