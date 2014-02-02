package wyvern.tools.lex;

import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.HasLocation;
import wyvern.tools.errors.ToolError;

public class InitialState implements ILexerState {
	
	private InitialState() {}
	private static InitialState instance = new InitialState();
	public static InitialState getInstance() {
		return instance;
	}
	
	/** Implementation invariant: either returns a tail call to an
	 * ILexerState.getToken() implementation, or else calls
	 * lexData.setLexerState() just before returning a concrete token,
	 * or else returns EOF.
	 */
	@Override
	public Token getToken(ILexInput input, ILexData lexData) {
		StringBuffer startOfLine = new StringBuffer();
		while (true) {
			// if end of file, return EOF token
			if (!input.hasNext()) {
				return Token.getEOF(input.getLocation());
			}
			
			char ch = input.peek();
			
			// on newline, reset the buffer and continue
			if (ch == '\n' || ch == '\r') {
				input.read();
				return getToken(input, lexData);
			}
			
			// skip non-newline whitespace
			if (Character.isWhitespace(ch)) {
				startOfLine.append(ch);
				input.read();
				continue;
			}
			
			// read identifiers
			if (Character.isAlphabetic(ch)) {
				lexData.setLexerState(IdentifierState.getInstance());
				return getNextToken(input, lexData, startOfLine.toString());
			}
			
			// read numbers
			if (Character.isDigit(ch)) {
				lexData.setLexerState(NumberState.getInstance());
				return getNextToken(input, lexData, startOfLine.toString());
			}
			
			// read strings
			if (ch == '"') {
				input.read();
				lexData.setLexerState(StringState.getInstance());
				return getNextToken(input, lexData, startOfLine.toString());
			}
			
			// read symbols
			if (LexUtils.isSymbol(ch)) {
				lexData.setLexerState(SymbolState.getInstance());
				return getNextToken(input, lexData, startOfLine.toString());
			}
			
			// else error
			ToolError.reportError(ErrorMessage.LEXER_ERROR, HasLocation.UNKNOWN);
			return null; // Unreachable.
		}
	}
	
	private Token getNextToken(ILexInput lexInput, ILexData lexData, String start) {
		Token token = null;
					
		// Doesn't work hugely well with changing amounts of spacing.
		if (start.equals(lexData.getCurrentPrefix())) {
			token = Token.getNEWLINE(lexInput.getLocation());
		} else if (start.startsWith(lexData.getCurrentPrefix())) {
			token = Token.getINDENT(lexInput.getLocation());
			lexData.pushPrefix(start);
		} else if (lexData.getCurrentPrefix().startsWith(start)) {
			lexData.popPrefix();
			if (!lexData.getCurrentPrefix().equals(start))
				lexData.setLexerState(InitialState.getInstance());
			token = Token.getDEDENT(lexInput.getLocation());
		} else {
			// This is commonly caused by inconsistent indentation (i.e. tabs vs spaces).  Best practice is to use spaces everywhere.
			ToolError.reportError(ErrorMessage.LEXER_ERROR, lexInput.getLocation());
		}
		
		return token;
	}
}
