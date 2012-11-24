package wyvern.tools.lexer;

import wyvern.tools.lexer.CommentState.CommentType;

public class SymbolState implements LexerState {

	private SymbolState() {}
	public static SymbolState getInstance() {
		return new SymbolState();
	}
	
	private StringBuffer buf = new StringBuffer();

	@Override
	public Token getToken(Lexer lexer) {
		if (lexer.hasNext()) {
			char ch = lexer.peek();
			
			// parse group symbols specially
			if (lexer.isGroupSymbol(ch)) {
				if (buf.length() == 0) {
					// read and return the symbol
					lexer.read();
					lexer.currentState = MidLineState.getInstance();
					return Token.getGroup(ch);
				} else {
					// return what is read so far; then get back to reading symbols
					lexer.currentState = SymbolState.getInstance();
					return Token.getIdentifier(buf.toString(), lexer.getLineNumber());
				}
			}
			
			// read other symbols
			if (lexer.isSymbol(ch)) {
				lexer.read();
				buf.append(ch);
				return getToken(lexer);
			}
		}
		
		// if end of file or not an ID char, set next lexer state and return number read
		lexer.currentState = MidLineState.getInstance();
		
		String symbol = buf.toString();
		
		//Support for comments
		if (symbol.startsWith("/*"))
			return CommentState.getInstance(CommentType.BlockComment).getToken(lexer);
		if (symbol.startsWith("//"))
			return CommentState.getInstance(CommentType.LineComment).getToken(lexer);
		
		return Token.getIdentifier(buf.toString(), lexer.getLineNumber());
	}

}
