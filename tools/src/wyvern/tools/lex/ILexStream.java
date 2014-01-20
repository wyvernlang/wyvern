package wyvern.tools.lex;

import java.io.InputStream;

public interface ILexStream {
	/**
	 * Returns the current token and advances the stream.  A special EOF token
	 * denotes the end of stream.
	 * 
	 * @return the current token
	 * 
	 * @throws ToolError if there is a lexing error
	 */
	Token next();
	
	/**
	 * Returns the current token.  Does not advance the stream.  A special EOF
	 * token denotes the end of stream.
	 * 
	 * @return the current token
	 * 
	 * @throws ToolError if there is a lexing error
	 */
	Token peek();	// returns the current token; does not advance
	
	/** Looks ahead n tokens.  peek() == lookAhead(0) */
	Token lookAhead(int n);
	
	/** Returns an ILexStream for parsing a DSL block.
	 * The intent is to call this immediately after next() returns a token of
	 * kind start. The returned LexStream will yield all tokens up to (but not
	 * including) the next token of kind end that is not internal to the DSL
	 * block.  If start==end, then doubled start/end tokens within the block
	 * are replaced with a single copy of the token, and do not end the DSL
	 * block.  If start!=end, then if a start token is encountered in the DSL
	 * block, a matching end token does not end the DSL block.
	 * 
	 * The returned ILexStream will yield an EOF instead of the final end
	 * token. The current ILexStream will yield the end token next.
	 * 
	 * @throws ToolError if there is a lexing error, e.g. the end token is not
	 * found
	 */
	ILexStream dslBlock(Token.Kind start, Token.Kind end);
	
	/** Returns a character stream to get the character-by-character input of
	 * this ILexStream.  If this ILexStream was a dslBlock() of another
	 * ILexStream, the leading indents will be removed from the InputStream.
	 * 
	 * TO BE IMPLEMENTED IN THE FUTURE
	 */
	InputStream asRawStream();

}
