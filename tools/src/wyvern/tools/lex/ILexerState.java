package wyvern.tools.lex;

public interface ILexerState {
	/** Implementation invariant: either returns a tail call to an
	 * ILexerState.getToken() implementation, or else calls
	 * lexData.setLexerState() just before returning a concrete token,
	 * or else returns EOF.
	 */
	Token getToken(ILexInput lexInput, ILexData lexData);	
}
