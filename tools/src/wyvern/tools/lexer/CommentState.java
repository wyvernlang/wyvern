package wyvern.tools.lexer;

public class CommentState implements LexerState {
	
	public static enum CommentType {
		BlockComment,
		LineComment
	}

	private CommentState() {}
	private static CommentState instance = new CommentState();
	public static CommentState getInstance(LexerState previous, CommentType type) {
		instance.previous = previous;
		instance.type = type;
		return instance;
	}
	public static CommentState getInstance(CommentType type) {
		instance.previous = null;
		instance.type = type;
		return instance;
	}
	
	private LexerState previous;
	private CommentType type;

	@Override
	public Token getToken(Lexer lexer) {
		//Used to find the end of block comments
		boolean blockStar = false;
		
		while (true) {
			if (!lexer.hasNext()) {
				return Token.getEOF();
			}
			
			char current = lexer.read();
			char next = lexer.peek();
			
			
			if (type == CommentType.LineComment && next=='\n') {
				//Requests the parser to get the next token
				return lexer.getToken();
			} else if (type == CommentType.BlockComment) {
				//If the * of the */ has been seen and the current char is a /
				if (blockStar && current == '/') {
					//If the previous state is defined, transition back to that
					if (previous != null)
						lexer.currentState = previous;
					
					//Return the next element seen
					return lexer.getToken();
				} else {
					if (current == '*')
						blockStar = true;
					else
						blockStar = false;
				}
			}
		}
	}

}
