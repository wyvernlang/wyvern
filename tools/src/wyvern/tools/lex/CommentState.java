package wyvern.tools.lex;

public class CommentState implements ILexerState {
	
	public static enum CommentType {
		BlockComment,
		LineComment
	}

	private CommentState(CommentType type) { this.type = type; }
	private static CommentState blockInstance = new CommentState(CommentType.BlockComment);
	private static CommentState lineInstance = new CommentState(CommentType.LineComment);
	public static CommentState getBlockInstance() {
		return blockInstance;
	}
	public static CommentState getLineInstance() {
		return lineInstance;
	}
	
	private final CommentType type;

	/** Implementation invariant: either returns a tail call to an
	 * ILexerState.getToken() implementation, or else calls
	 * lexData.setLexerState() just before returning a concrete token,
	 * or else returns EOF.
	 */
	@Override
	public Token getToken(ILexInput lexer, ILexData lexData) {
		//Used to find the end of block comments
		boolean blockStar = false;
		
		while (true) {
			if (!lexer.hasNext()) {
				lexData.setLexerState(InitialState.getInstance());
				return Token.getEOF();
			}
			
			char current = lexer.read();
			char next = lexer.peek();
			
			
			if (type == CommentType.LineComment && next=='\n') {
				//Requests the parser to get the next token
				return MidLineState.getInstance().getToken(lexer, lexData);
			} else if (type == CommentType.BlockComment) {
				//If the * of the */ has been seen and the current char is a /
				if (blockStar && current == '/') {
					//If the previous state is defined, transition back to that
					//if (previous != null)
					//	lexData.setLexerState(previous);
					
					//Return the next element seen
					return MidLineState.getInstance().getToken(lexer, lexData);
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
