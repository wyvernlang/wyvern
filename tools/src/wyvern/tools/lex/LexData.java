package wyvern.tools.lex;

import java.util.Stack;

class LexData implements ILexData {
	private ILexerState currentState = InitialState.getInstance();
	private Stack<String> prefixStack = new Stack<String>();

	public LexData() {
		prefixStack.push("");
	}

	@Override
	public String getCurrentPrefix() {
		return prefixStack.peek();
	}

	@Override
	public void pushPrefix(String newPrefix) {
		prefixStack.push(newPrefix);
	}

	@Override
	public void popPrefix() {
		prefixStack.pop();
	}
	
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
	@Override
	public void setLexerState(ILexerState state) {
		currentState = state;
	}

	@Override
	public ILexerState getLexerState() {
		return currentState;
	}

}
