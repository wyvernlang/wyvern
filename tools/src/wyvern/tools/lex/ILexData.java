package wyvern.tools.lex;

interface ILexData {
	void pushPrefix(String newPrefix);
	void popPrefix();
	String getCurrentPrefix();
	
	/** Typically clients should call setLexerState and pass
	 * MidLineState.getInstance() as the argument.  The main exception is that
	 * MidLineState may set InitialState after reading a newline.
	 * 
	 * The lexer can get into other states, but they are transient (used only
	 * during the lexing of the current token) and do not need to be recorded
	 * in this data structure. 
	 * 
	 * @param state the new lexer state
	 */
	void setLexerState(ILexerState state);
	ILexerState getLexerState();
}
