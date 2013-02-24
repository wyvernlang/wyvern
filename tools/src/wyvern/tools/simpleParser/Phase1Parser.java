package wyvern.tools.simpleParser;

import java.io.Reader;
import java.util.ArrayList;

import wyvern.tools.lexer.Lexer;
import wyvern.tools.lexer.Token;
import wyvern.tools.rawAST.*;
import static wyvern.tools.lexer.Token.Kind.*;
import static wyvern.tools.errors.ErrorMessage.*;
import static wyvern.tools.errors.ToolError.reportError;

public class Phase1Parser {
	public static RawAST parse(Reader r) {
		return parseTopSequence(new Lexer(r));
	}
	
	/** Parses an element.  That is, either:
	 *    - an identifier
	 *    - a symbol
	 *    - a number
	 *    - a string
	 *    - a parenthesized expression
	 *    
	 *  We leave the token after the element in the stream.
	 *    
	 * @param lexer
	 * @return
	 */
	public static RawAST parseElement(Lexer lexer) {
		Token token = lexer.getToken();
		
		if (token.kind == EOF) {
			reportError(EXPECTED_TOKEN_NOT_EOF, token);
		}
		
		if (token.kind == Identifier) {
			return new Symbol(token.text);
		}
		
		if (token.kind == Symbol) {
			return new Symbol(token.text);
		}
		
		if (token.kind == Number) {
			return new Int(token.value);
		}
		
		if (token.kind == String) {
			return new StringNode(token.text);
		}
		
		if (token.kind == LPAREN) {
			Token openParen = token;
			// read elements in the middle
			Sequence sequence = new Parenthesis(new ArrayList<RawAST>());
			parseSequence(lexer, sequence);	// TODO: maybe call a separate parseSequence that ignores newlines and indents?
			
			token = lexer.getToken();
			if (token.kind == RPAREN) {
				return sequence;
			} else {
				reportError(MISMATCHED_PARENTHESES, openParen);
			}
		}
		
		// TODO: other forms
		
		throw new RuntimeException("parsing case not implemented");
	}

	/** Parses a sequence of elements.  This sequence will include the line itself
	 * and any indented lines afterwards.  It will end with one of:
	 *   - a RPAREN
	 *   - a NEWLINE
	 *   - a DEDENT
	 *   - an EOF
	 *   
	 *   If the terminating symbol (one of the 4 above) is a NEWLINE, we eat it.
	 *   Otherwise, we leave it in the stream for the caller to handle.
	 * 
	 * @param lexer
	 * @return
	 */
	private static void parseSequence(Lexer lexer, Sequence result) {
		Token token = lexer.peekToken();

		while (token.kind != RPAREN && token.kind != EOF && token.kind != DEDENT && token.kind != NEWLINE) {
		
			if (token.kind == INDENT) {
				// eat the token, read a line sequence, and check for a DEDENT
				token = lexer.getToken();
				LineSequence node = new LineSequence();
				parseLines(lexer, node);
				token = lexer.peekToken();
				// accept an EOF as a DEDENT
				if (token.kind == DEDENT || token.kind == EOF) {
					// eat the DEDENT
					if (token.kind == DEDENT)
						token = lexer.getToken();
					result.children.add(node);
					return;
				} else {
					reportError(INDENT_DEDENT_MISMATCH, token);
				}
			} else {
				RawAST node = parseElement(lexer);
				result.children.add(node);
			}
			token = lexer.peekToken();
		}
		/*
		if (token.kind == RPAREN || token.kind == EOF || token.kind == DEDENT || token.kind == NEWLINE)
			return UnitData.getInstance();
		
		if (token.kind == INDENT) {
			// eat the token, read a line sequence, and check for a DEDENT
			token = lexer.getToken();
			Data indentedLines = parseLines(lexer);
			token = lexer.peekToken();
			// accept an EOF as a DEDENT
			if (token.kind == DEDENT || token.kind == EOF) {
				// eat the DEDENT
				if (token.kind == DEDENT)
					token = lexer.getToken();
				return new ListData(new ListData(new SymbolData("INDENT"),indentedLines), UnitData.getInstance());
			} else {
				throw new ParseException(); // mismatched INDENT/DEDENT
			}			
		}
		
		Data first = parseElement(lexer);
		Data rest = parseSequence(lexer);
		
		//if (token.kind != INDENT)
			return new ListData(first, rest);
		//else 
		//	return new ListData(new ListData(new SymbolData("INDENT"),first), rest);
		*/
	}
	
	private static void parseLines(Lexer lexer, Sequence result) {
		Token token = lexer.peekToken();
		
		while (true) { // token.kind != EOF && token.kind != DEDENT) {
			
			// read past any newlines
			while (token.kind == NEWLINE) {
				lexer.getToken();
				token = lexer.peekToken();
			}
			
			if (token.kind == EOF || token.kind == DEDENT)
				break;
			
			Line line = new Line(new ArrayList<RawAST>());
			parseSequence(lexer, line);
			result.children.add(line);
			token = lexer.peekToken();
		}
		/*
		// are we done reading this sequence of lines?
		if (token.kind == EOF || token.kind == DEDENT) {
			return new TopSequenceNode(new ArrayList<RawASTNode>());
		}

		
		Data rest = parseLines(lexer);
		
		if (token.kind != INDENT)
			return new ListData(first, rest);
		else 
			return new ListData(first,new ListData(first, rest));
		*/
	}

	
	/** Parses a file.  There is some special case code to deal with how the
	 * token sequence in a file begins, then we parse a sequence of lines.
	 * 
	 * 
	 * 
	 * @param lexer
	 * @return
	 */
	public static RawAST parseTopSequence(Lexer lexer) {
		/*Token token = lexer.getToken();
		
		if (token.kind == EOF) {
			return UnitData.getInstance();
		}
		
		// special case for a top-level file: the lexer stream always starts with a NEWLINE (for engineering reasons)
		if (token.kind != NEWLINE) {
			throw new ParseException();
		}*/
		
		LineSequence topSequence = new LineSequence();
		parseLines(lexer, topSequence);		
		return topSequence;
	}
}
