import java.util.regex.Matcher;
import java.util.regex.Pattern;

import wyvern.tools.parsing.transformers.*;
import wyvern.tools.typedAST.core.*;
import wyvern.tools.typedAST.interfaces.*;
import wyvern.tools.typedAST.core.expressions.*;
import wyvern.tools.typedAST.core.binding.*;
import wyvern.tools.typedAST.core.values.*;
import wyvern.tools.typedAST.extensions.*;
import wyvern.tools.typedAST.core.declarations.*;
import wyvern.tools.typedAST.abs.*;
import wyvern.tools.types.*;
import wyvern.tools.types.extensions.*;
import wyvern.tools.util.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.HashMap;
import java.util.*;
import wyvern.tools.errors.FileLocation;
import java.net.URI;
import edu.umn.cs.melt.copper.runtime.logging.CopperParserException;
import wyvern.tools.parsing.coreparser.Token;
import static wyvern.tools.parsing.coreparser.WyvernParserConstants.*;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.ToolError;

%%
%parser WyvernLexer

%aux{
	/********************** LEXER STATE ************************/
	boolean foundTilde = false;						// is there a tilde ~ in the current line?
	boolean DSLNext = false;						// is the next line a DSL?
	boolean inDSL = false;							// are we in a DSL?
	Stack<String> indents = new Stack<String>();	// the stack of indents
	
	/********************** HELPER FUNCTIONS ************************/
	
    FileLocation loc(Token t) {
        return new FileLocation(virtualLocation.getFileName(), t.beginLine, t.beginColumn);
    }
	
	/** @return 1 for an indent, -n for n dedents, or 0 for the same indentation level
	 */
	int adjustIndent(String newIndent, Token tokenLoc) throws CopperParserException {
		String currentIndent = indents.peek();
		if (newIndent.length() < currentIndent.length()) {
			// dedent(s)
			int dedentCount = 0;
			while (newIndent.length() < currentIndent.length()) {
				indents.pop();
				currentIndent = indents.peek();
				dedentCount--;
			}
			if (newIndent.equals(currentIndent))
				return dedentCount;
			else
				ToolError.reportError(ErrorMessage.INCONSISTENT_INDENT, loc(tokenLoc));
                throw new CopperParserException("Illegal dedent at line "+tokenLoc.beginLine+": does not match any previous indent level");
		} else if (newIndent.length() > currentIndent.length()) {
			// indent
			if (newIndent.startsWith(currentIndent)) {
				indents.push(newIndent);
				return 1;
			} else {
				throw new CopperParserException("Illegal indent at line "+tokenLoc.beginLine+": not a superset of previous indent level");
			}
		} else {
			return 0;
		}
	}

	/** Adjusts the indentation level to the baseline (no indent)
	 * @return the list of dedent tokens that must be added to the
	 * stream to reach the baseline indent (empty if no dedents)
	 */ 
	List<Token> possibleDedentList(Token tokenLoc) throws CopperParserException {
		int levelChange = adjustIndent("", tokenLoc);
  		List<Token> tokenList = emptyList(); 
		while (levelChange < 0) {
			Token t = makeToken(DEDENT,"",tokenLoc);
			tokenList.add(t);
			levelChange++;
		}
  		return tokenList;
  	}	
	
	/**
	 * creates indents/dedents at the beginning if necessary
	 * creates a NEWLINE at the end if necessary
	 */
	LinkedList<Token> adjustLogicalLine(LinkedList<Token> aLine) throws CopperParserException {
		if (hasNonSpecialToken(aLine)) {
			// it's a logical line...let's adjust it!
			
			// find the indent for this line
			Token firstToken = aLine.getFirst();
			String lineIndent = "";
			if (firstToken.kind == WHITESPACE)
				lineIndent = firstToken.image;
			
			// add indents/dedents as needed
			int levelChange = adjustIndent(lineIndent, firstToken);
			if (levelChange == 1)
				aLine.addFirst(makeToken(INDENT,"",firstToken));
			while (levelChange < 0) {
				aLine.addFirst(makeToken(DEDENT,"",firstToken));
				levelChange++;
			}
			
			// add a NEWLINE at the end
			Token lastToken = aLine.getLast();
			Token NL = makeToken(NEWLINE,"",lastToken);
			aLine.addLast(NL);
		}
		    // ELSE do nothing: this line has only comments/whitespace
		
		return aLine;
	}
	
	/** convenient construction function for tokens.  The location of
	 * the new token is taken from the tokenLoc argument
	 */
	Token makeToken(int kind, String s, Token tokenLoc) {
		return makeToken(kind, s,tokenLoc.beginLine, tokenLoc.beginColumn);
	}
	
	/** convenient construction function for tokens.
	 */
	Token makeToken(int kind, String s, int beginLine, int beginColumn) {
		Token t = new Token(kind, s);
		t.beginLine = beginLine;
		t.beginColumn = beginColumn;
		return t;
	}
	
	/** Wraps the lexeme s in a Token, setting the begin line/column and kind appropriately
	 *  The current lexical location is used.
	 */
	Token token(int kind, String s) {
		// Copper starts counting columns at 0, but we want to follow convention and count columns starting at 1
		return makeToken(kind, s, virtualLocation.getLine(), virtualLocation.getColumn()+1);
	}
	
	/** Constructor for an empty list of Tokens.
	 */
	List<Token> emptyList() {
		return new LinkedList<Token>();
	}
	
	/** Constructor for a singleton list of Tokens.
	 */
	List<Token> makeList(Token t) {
		List<Token> l = emptyList();
		l.add(t);
		return l;
	}

    /** @return true if there are tokens other than comments and whitespace in this token list
     */ 	
	boolean hasNonSpecialToken(List<Token> l) {
		for (Token t : l)
			if (!LexerUtils.isSpecial(t))
				return true; 
		return false;
	}
	
    /**
     * Find occurrences of escape sequences in the input string and replaces them with the
     * appropriate character.
     */
    String replaceEscapeSequences(String s) {
        StringBuilder new_s = new StringBuilder();

        int i;
        for (i = 0; i < s.length() - 1; ++i) {
            char c = s.charAt(i);

            if (c == '\\') {
                switch (s.charAt(i + 1)) {
                    case '\'':
                        c = '\''; ++i;
                        break;

                    case '\"':
                        c = '\"'; ++i;
                        break;

                    case '\\':
                        c = '\\'; ++i;
                        break;

                    case 'b':
                        c = '\b'; ++i;
                        break;

                    case 'f':
                        c = '\f'; ++i;
                        break;

                    case 'n':
                        c = '\n'; ++i;
                        break;

                    case 'r':
                        c = '\r'; ++i;
                        break;

                    case 't':
                        c = '\t'; ++i;
                        break;

                    default:
                        ToolError.reportError(ErrorMessage.ILLEGAL_ESCAPE_SEQUENCE,
                                              new FileLocation(virtualLocation.getFileName(),
                                                               virtualLocation.getLine(),
                                                               virtualLocation.getColumn() + i + 2));
                }
            }

            new_s.append(c);
        }

        if (i < s.length()) {
            char c = s.charAt(i);
            if (c == '\\') {
                ToolError.reportError(ErrorMessage.UNCLOSED_STRING_LITERAL,
                                      new FileLocation(virtualLocation.getFileName(),
                                                       virtualLocation.getLine(),
                                                       virtualLocation.getColumn() + 1));
            }

            new_s.append(s.charAt(i));
        }

        return new_s.toString();
    }

%aux}

%init{
	// start with the baseline indentation level
	indents.push("");
%init}

%lex{
	class keywds;
    class specialNumbers;
    
    terminal Token whitespace_t ::= /[ \t]+/ {: RESULT = token(WHITESPACE,lexeme); :};
    terminal Token dsl_indent_t ::= /[ \t]+/ {: RESULT = token(WHITESPACE,lexeme); :};
    terminal Token indent_t ::= /[ \t]+/ {: RESULT = token(WHITESPACE,lexeme); :};

    terminal Token newline_t ::= /(\n|(\r\n))/ {: RESULT = token(WHITESPACE,lexeme); :};
    
    terminal Token continue_line_t ::= /\\(\n|(\r\n))/ {: RESULT = token(WHITESPACE,lexeme); :};

	terminal Token comment_t  ::= /\/\/([^\r\n])*/ {: RESULT = token(SINGLE_LINE_COMMENT,lexeme); :};
	terminal Token multi_comment_t  ::= /\/\*([^*]|\*[^/])*\*\// {: RESULT = token(MULTI_LINE_COMMENT,lexeme); :};
	
	
	
 	terminal Token identifier_t ::= /[a-zA-Z_][a-zA-Z_0-9]*/ in (), < (keywds), > () {:
 		RESULT = token(IDENTIFIER,lexeme);
 	:};

    terminal Token classKwd_t ::= /class/ in (keywds) {: RESULT = token(CLASS,lexeme); :};
	terminal Token typeKwd_t 	::= /type/ in (keywds) {: RESULT = token(TYPE,lexeme); :};
	terminal Token valKwd_t 	::= /val/ in (keywds) {: RESULT = token(VAL,lexeme); :};
	terminal Token defKwd_t 	::= /def/ in (keywds) {: RESULT = token(DEF,lexeme); :};
	terminal Token varKwd_t 	::= /var/ in (keywds) {: RESULT = token(VAR,lexeme); :};
	terminal Token delegateKwd_t::= /delegate/ in (keywds) {: RESULT = token(DELEGATE,lexeme); :};
	terminal Token toKwd_t		::= /to/ in (keywds) {: RESULT = token(TO,lexeme); :};
	//terminal Token Kwd_t 	::= /fn/ in (keywds) {: RESULT = token(FN,lexeme); :};
	terminal Token requireKwd_t 	::= /require/ in (keywds) {: RESULT = token(REQUIRE,lexeme); :};
	terminal Token metadataKwd_t 	::= /metadata/ in (keywds) {: RESULT = token(METADATA,lexeme); :};
	terminal Token newKwd_t 	::= /new/ in (keywds) {: RESULT = token(NEW,lexeme); :};
 	terminal Token importKwd_t   ::= /import/ in (keywds) {: RESULT = token(IMPORT,lexeme); :};
 	terminal Token moduleKwd_t   ::= /module/ in (keywds) {: RESULT = token(MODULE,lexeme); :};
 	terminal Token comprisesKwd_t   ::= /comprises/ in (keywds) {: RESULT = token(COMPRISES,lexeme); :};
 	terminal Token extendsKwd_t   ::= /extends/ in (keywds) {: RESULT = token(EXTENDS,lexeme); :};
	terminal Token ifKwd_t 	::= /if/ in (keywds);
 	terminal Token thenKwd_t   ::= /then/ in (keywds);
 	terminal Token elseKwd_t   ::= /else/ in (keywds);
 	terminal Token objtypeKwd_t   ::= /objtype/ in (keywds);
 	
 	terminal Token resourceKwd_t    ::= /resource/ in (keywds) {: RESULT = token(RESOURCE,lexeme); :};
 	terminal Token asKwd_t ::= /as/ in (keywds) {: RESULT = token(AS,lexeme); :};
 	terminal Token instantiateKwd_t ::= /instantiate/ in (keywds) {: RESULT = token(INSTANTIATE,lexeme); :};

	terminal Token taggedKwd_t  ::= /tagged/  in (keywds) {: RESULT = token(TAGGED,lexeme); :};
    terminal Token matchKwd_t   ::= /match/   in (keywds) {: RESULT = token(MATCH,lexeme); :};
    terminal Token defaultKwd_t ::= /default/ in (keywds);
    terminal Token caseKwd_t ::= /case/ in (keywds);
    terminal Token ofKwd_t ::= /of/ in (keywds);

 	terminal Token booleanLit_t ::= /true|false/ in (keywds) {: RESULT = token(BOOLEAN_LITERAL,lexeme); :};
 	terminal Token decimalInteger_t ::= /([1-9][0-9]*)|0/  {: RESULT = token(DECIMAL_LITERAL,lexeme); :};

	terminal Token tilde_t ::= /~/ {: RESULT = token(TILDE,lexeme); :};
	terminal Token plus_t ::= /\+/ {: RESULT = token(PLUS,lexeme); :};
	terminal Token dash_t ::= /-/ {: RESULT = token(DASH,lexeme); :};
	terminal Token mult_t ::= /\*/ {: RESULT = token(MULT,lexeme); :};
	terminal Token divide_t ::= /\// {: RESULT = token(DIVIDE,lexeme); :};
	terminal Token remainder_t ::= /%/ {: RESULT = token(MOD,lexeme); :};
	terminal Token equals_t ::= /=/ {: RESULT = token(EQUALS,lexeme); :};
	terminal Token equalsequals_t ::= /==/ {: RESULT = token(EQUALSEQUALS,lexeme); :};
	terminal Token openParen_t ::= /\(/ {: RESULT = token(LPAREN,lexeme); :};
 	terminal Token closeParen_t ::= /\)/ {: RESULT = token(RPAREN,lexeme); :};
 	terminal Token comma_t ::= /,/  {: RESULT = token(COMMA,lexeme); :};
 	terminal Token arrow_t ::= /=\>/  {: RESULT = token(ARROW,lexeme); :};
 	terminal Token tarrow_t ::= /-\>/  {: RESULT = token(TARROW,lexeme); :};
 	terminal Token dot_t ::= /\./ {: RESULT = token(DOT,lexeme); :};
 	terminal Token colon_t ::= /:/ {: RESULT = token(COLON,lexeme); :};
 	terminal Token pound_t ::= /#/ {: RESULT = token(POUND,lexeme); :};
 	terminal Token question_t ::= /?/ {: RESULT = token(QUESTION,lexeme); :};
 	terminal Token bar_t ::= /\|/ {: RESULT = token(BAR,lexeme); :};
 	terminal Token and_t ::= /&/ {: RESULT = token(AND,lexeme); :};
 	terminal Token gt_t ::= />/ {: RESULT = token(GT,lexeme); :};
 	terminal Token lt_t ::= /</ {: RESULT = token(LT,lexeme); :};
    terminal Token oSquareBracket_t ::= /\[/ {: RESULT = token(LBRACK,lexeme); :};
    terminal Token cSquareBracket_t ::= /\]/ {: RESULT = token(RBRACK,lexeme); :};
    terminal Token booleanand_t ::= /&&/ {: RESULT = token(BOOLEANAND,lexeme); :};
    terminal Token booleanor_t ::= /\|\|/ {: RESULT = token(BOOLEANOR,lexeme); :};

 	terminal Token shortString_t ::= /(('([^'\n]|\\.|\\O[0-7])*')|("([^"\n]|\\.|\\O[0-7])*"))|(('([^']|\\.)*')|("([^"]|\\.)*"))/ {:
 		RESULT = token(STRING_LITERAL, replaceEscapeSequences(lexeme.substring(1,lexeme.length()-1)));
 	:};

 	terminal Token oCurly_t ::= /\{/ {: RESULT = token(LBRACE,lexeme); :};
 	terminal Token cCurly_t ::= /\}/ {: RESULT = token(RBRACE,lexeme); :};
 	terminal notCurly_t ::= /[^\{\}]*/ {: RESULT = lexeme; :};
 	
    
 	terminal Token dslLine_t ::= /[^\n]*(\n|(\r\n))/ {: RESULT = token(DSLLINE,lexeme); :};
 	
 	// error if DSLNext but not indented further
 	// DSL if DSLNext and indented (unsets DSLNext, sets inDSL)
 	// DSL if inDSL and indented
 	// indent_t otherwise 
	disambiguate d1:(dsl_indent_t,indent_t)
	{:
		String currentIndent = indents.peek();
		if (lexeme.length() > currentIndent.length() && lexeme.startsWith(currentIndent)) {
			// indented
			if (DSLNext || inDSL) {
				DSLNext = false;
				inDSL = true;
				return dsl_indent_t;
			} else {
				return indent_t;
			}
		}
		if (DSLNext)
			throw new CopperParserException("Indicated DSL with ~ but then did not indent");
		inDSL = false;
		return indent_t;
	:};
%lex}

%cf{
    non terminal innerdsl;
    non terminal inlinelit;
	non terminal List<Token> program;
	non terminal List<Token> lines;
	non terminal List<Token> logicalLine;
	non terminal List<Token> dslLine;
	non terminal List<Token> anyLineElement;
	non terminal List<Token> nonWSLineElement;
	non terminal List lineElementSequence;
	non terminal List<Token> parens;
	non terminal List<Token> parenContent;
	non terminal List<Token> parenContents;
	non terminal List<Token> optParenContents;
	non terminal Token operator;
	non terminal Token literal;
	non terminal Token keyw;
	non terminal List<Token> aLine;

	start with program;
	
	parenContent ::= anyLineElement:e {: RESULT = e; :}
	               | newline_t:t {: RESULT = makeList(t); :};
	
	parenContents ::= parenContent:p {: RESULT = p; :}
	                | parenContents:ps parenContent:p {: RESULT = ps; ps.addAll(p); :};
	                
	optParenContents ::= parenContents:ps {: RESULT = ps; :}
	                   | {: RESULT = emptyList(); :};
	
	parens ::= openParen_t:t1 optParenContents:list closeParen_t:t2 {: RESULT = makeList(t1); RESULT.addAll(list); RESULT.add(t2); :}
	         | oSquareBracket_t:t1 optParenContents:list cSquareBracket_t:t2  {: RESULT = makeList(t1); RESULT.addAll(list); RESULT.add(t2); :};
	
	keyw ::= classKwd_t:t {: RESULT = t; :}
	       | typeKwd_t:t {: RESULT = t; :}
	       | valKwd_t:t {: RESULT = t; :}
	       | defKwd_t:t {: RESULT = t; :}
	       | varKwd_t:t {: RESULT = t; :}
	       | delegateKwd_t:t {: RESULT = t; :}
	       | toKwd_t:t {: RESULT = t; :}
//	       | fnKwd_t:t {: RESULT = t; :}
	       | requireKwd_t:t {: RESULT = t; :}
	       | metadataKwd_t:t {: RESULT = t; :}
	       | newKwd_t:t {: RESULT = t; :}
	       | moduleKwd_t:t {: RESULT = t; :}
	       | comprisesKwd_t:t {: RESULT = t; :}
	       | extendsKwd_t:t {: RESULT = t; :}
	       | matchKwd_t:t {: RESULT = t; :}
	       | taggedKwd_t:t {: RESULT = t; :}
	       | importKwd_t:t {: RESULT = t; :}
	       | instantiateKwd_t:t {: RESULT = t; :}
	       | asKwd_t:t {: RESULT = t; :}
	       | resourceKwd_t:t {: RESULT = t; :};
//	       | :t {: RESULT = t; :}

	literal ::= decimalInteger_t:t {: RESULT = t; :}
	          | booleanLit_t:t {: RESULT = t; :}
	          | shortString_t:t {: RESULT = t; :}
	          | inlinelit:lit {: RESULT = RESULT = token(DSL_LITERAL,(String)lit); :};
	
	operator ::= tilde_t:t {: foundTilde = true; RESULT = t; :}
	           | plus_t:t {: RESULT = t; :}
	           | dash_t:t {: RESULT = t; :}
	           | mult_t:t {: RESULT = t; :}
	           | divide_t:t {: RESULT = t; :}
	           | remainder_t:t {: RESULT = t; :}
	           | equals_t:t {: RESULT = t; :}
             | equalsequals_t:t {: RESULT = t; :}
	           | comma_t:t {: RESULT = t; :}
	           | arrow_t:t {: RESULT = t; :}
	           | tarrow_t:t {: RESULT = t; :}
	           | dot_t:t {: RESULT = t; :}
	           | colon_t:t {: RESULT = t; :}
	           | pound_t:t {: RESULT = t; :}
	           | question_t:t {: RESULT = t; :}
	           | bar_t:t {: RESULT = t; :}
	           | and_t:t {: RESULT = t; :}
	           | gt_t:t {: RESULT = t; :}
	           | lt_t:t {: RESULT = t; :}
             | equalsequals_t:t {: RESULT = t; :}
             | booleanand_t:t {: RESULT = t; :}
             | booleanor_t:t {: RESULT = t; :}
	           ;
	           
	anyLineElement ::= whitespace_t:n {: RESULT = makeList(n); :}
	                 | nonWSLineElement:n {: RESULT = n; :};
	                 
	// a non-whitespace line element 
	nonWSLineElement ::= identifier_t:n {: RESULT = makeList(n); :}
	                   | comment_t:n {: RESULT = makeList(n); :}
	                   | multi_comment_t:n {: RESULT = makeList(n); :}
	                   | continue_line_t:t  {: RESULT = makeList(t); :}
	                   | operator:t {: RESULT = makeList(t); :}
	                   | literal:t {: RESULT = makeList(t); :}
	                   | keyw:t {: RESULT = makeList(t); :}
	                   | parens:l {: RESULT = l; :};

    dslLine ::= dsl_indent_t:t dslLine_t:line {: RESULT = makeList(t); RESULT.add(line); :};
	
    inlinelit ::= oCurly_t innerdsl:idsl cCurly_t {: RESULT = idsl; :};
    innerdsl ::= notCurly_t:str {: RESULT = str; :} | notCurly_t:str oCurly_t innerdsl:idsl cCurly_t innerdsl:stre {: RESULT = str + "{" + idsl + "}" + stre; :} | {: RESULT = ""; :};
    
	lineElementSequence ::= indent_t:n {: RESULT = makeList(n); :}
	                      | nonWSLineElement:n {:
	                            // handles lines that start without any indent
								if (DSLNext)
									throw new CopperParserException("Indicated DSL with ~ but then did not indent");
	                      		RESULT = n;
	                        :}
	                      | lineElementSequence:list anyLineElement:n {: list.addAll(n); RESULT = list; :};
	
	logicalLine ::= lineElementSequence:list newline_t:n {:
						list.add(n);
						List<Token> adjustedList = adjustLogicalLine((LinkedList<Token>)list);
					    if (foundTilde) {
						    DSLNext = true;
						    foundTilde = false;
						}
						RESULT = list;
					:}
				  | newline_t:n {: RESULT = makeList(n); :}; // an empty line
				  
	aLine ::= dslLine:line {: RESULT = line; :}
	        | logicalLine:line  {: RESULT = line; :}; 
	          
	lines ::= logicalLine:line {: RESULT = line; :}
	        | lines:p aLine:line {: p.addAll(line); RESULT = p; :};
	          
	program ::= lines:p {:
	             	RESULT = p;
	             	Token t = ((LinkedList<Token>)p).getLast();
	             	RESULT.addAll(possibleDedentList(t));
	           	:}
	          | lines:p lineElementSequence:list {:
	          		// handle the case of ending in an incomplete line
	          		RESULT = p;
	          		List<Token> adjustedList = adjustLogicalLine((LinkedList<Token>)list);
	          		RESULT.addAll(adjustedList);
	             	Token t = ((LinkedList<Token>)adjustedList).getLast();
	          		RESULT.addAll(possibleDedentList(t));
	          	:}
	          | lineElementSequence:list {: RESULT = list; :} // a single-line program with no carriage return
	          | /* empty program */ {: RESULT = emptyList(); :};
	
%cf}
