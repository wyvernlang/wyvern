package wyvern.tools.lex;

import wyvern.tools.lex.CommentState.CommentType;

public class SymbolState implements ILexerState {

	private SymbolState() {}
	private final static SymbolState instance = new SymbolState();
	public static SymbolState getInstance() {
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
				
				// parse group symbols specially
				if (LexUtils.isGroupSymbol(ch)) {
					if (buf.length() == 0) {
						// read and return the symbol
						lexer.read();
						lexData.setLexerState(MidLineState.getInstance());
						return Token.getGroup(ch, lexer.getLocation());
					} else {
						// return what is read so far; then get back to reading symbols
						lexData.setLexerState(SymbolState.getInstance());
						return Token.getIdentifier(buf.toString(), lexer.getLocation());
					}
				}
				
				// read other symbols
				if (LexUtils.isSymbol(ch)) {
					lexer.read();
					buf.append(ch);
					continue;
				}
			}
			
			
			String symbol = buf.toString();
			
			//Support for comments
			if (symbol.startsWith("/*"))
				return CommentState.getBlockInstance().getToken(lexer, lexData);
			if (symbol.startsWith("//"))
				return CommentState.getLineInstance().getToken(lexer, lexData);
			
			// if end of file or not an ID char, set next lexer state and return number read
			lexData.setLexerState(MidLineState.getInstance());
			
			return Token.getIdentifier(buf.toString(), lexer.getLocation());
		}
	}

}
