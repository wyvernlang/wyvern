import edu.umn.cs.melt.copper.runtime.logging.CopperParserException;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.ToolError;
import wyvern.tools.parsing.coreparser.Token;
import wyvern.tools.parsing.coreparser.WyvernParserConstants;

import static wyvern.tools.parsing.coreparser.WyvernParserConstants.*;

%%
%parser WyvernLexer

%aux{
	/********************** LEXER STATE ************************/
	boolean foundTilde = false;						// is there a tilde ~ in the current line?
	boolean DSLNext = false;						// is the next line a DSL?
	boolean inDSL = false;							// are we in a DSL?
	Stack<String> indents = new Stack<String>();	// the stack of indents
	Token flagTok = null;							// a token that signals whether an indent is for a DSL
	Token lastIndent = null;
    public FileLocation startLocation = null;
    boolean ilineNext = false;                      // is the first iline in a sequence next?
    boolean isEQARROWlast = false;
    Stack<String> lambdas = new Stack<String>();	// the stack of indents for lambdas
    Stack<Stack<String>> metaStack = new Stack<Stack<String>>(); // the stack of indentation stacks, for handling multi-line lambdas correctly
	
	/********************** HELPER FUNCTIONS ************************/

    void adjustEQARROW(List list) {
        if (list.size() > 0) {
            Token t = (Token) list.get(list.size()-1);
            if (t.kind == EQARROW) {
                isEQARROWlast = true;
            } else if (t.kind != WHITESPACE && t.kind != SINGLE_LINE_COMMENT
                        && t.kind != MULTI_LINE_COMMENT) {
                isEQARROWlast = false;
            }
        }
    }
    
	/** equivalent (except for "if") to DSLNext */
	boolean isDSLNext() {
	    if (flagTok == null)
	    	return true;
		switch (flagTok.kind) {
		  case TILDE:
		  		return true;
		  case TYPE:
		  case DATATYPE:
		  case DEF:
		  case NEW:
		  case MATCH:
		  case REC:
		  case EQARROW:
		  		return false;
		  default:
		  		throw new RuntimeException("broke invariant!");
		}
	}

	/** Wraps the lexeme s in a Token, setting the begin line/column and kind appropriately
	 *  The current lexical location is used.
	 */
	Token token(int kind, String s) {
		// Copper starts counting columns at 0, but we want to follow convention and count columns starting at 1
		int startLine = startLocation==null?1:startLocation.getLine();
        int startChar = startLocation==null?0:startLocation.getCharacter();
        return LexerUtils.makeToken(kind, s, virtualLocation.getLine()+startLine-1, virtualLocation.getColumn()+startChar+1);
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
    terminal Token iindent_t ::= /[ \t]+/ {:
        RESULT = token(WHITESPACE,lexeme);
        if (ilineNext) {
            lambdas.push(lexeme);
            ilineNext = false;
        }
    :};

    terminal Token newline_t ::= /(\n|(\r\n))/ {: RESULT = token(WHITESPACE,lexeme); :};
    terminal Token inewline_t ::= /(\n|(\r\n))/ {: RESULT = token(WHITESPACE,lexeme); :};
    terminal Token fn_newline_t ::= /(\n|(\r\n))/ {:
        ilineNext = true;
        RESULT = token(WHITESPACE,lexeme);
        // save a copy of the indent Stack, adjust the stack to the current indent level
        metaStack.push((Stack<String>) indents.clone());
        String thisIndent = "";
        if (lastIndent != null) {
            thisIndent = lastIndent.image;
        }
        LexerUtils.adjustIndent(thisIndent, RESULT, virtualLocation.getFileName(), indents);
    :};
    
    terminal Token continue_line_t ::= /\\(\n|(\r\n))/ {: RESULT = token(WHITESPACE,lexeme); :};

	terminal Token comment_t  ::= /\/\/([^\r\n])*/ {: RESULT = token(SINGLE_LINE_COMMENT,lexeme); :};
	terminal Token multi_comment_t  ::= /\/\*([^*]|\*[^/])*\*\// {: RESULT = token(MULTI_LINE_COMMENT,lexeme); :};

 	terminal Token identifier_t ::= /[a-zA-Z_][a-zA-Z_0-9]*/ in (), < (keywds), > () {:
 		RESULT = token(IDENTIFIER,lexeme);
 	:};

    terminal Token classKwd_t ::= /class/ in (keywds) {: RESULT = token(CLASS,lexeme); :};
	terminal Token typeKwd_t 	::= /type/ in (keywds) {: RESULT = token(TYPE,lexeme); flagTok = RESULT; :};

	terminal Token datatypeKwd_t ::= /datatype/ in (keywds) {: RESULT = token(DATATYPE, lexeme); flagTok = RESULT; :};

	terminal Token valKwd_t 	::= /val/ in (keywds) {: RESULT = token(VAL,lexeme); :};
	terminal Token defKwd_t 	::= /def/ in (keywds) {: RESULT = token(DEF,lexeme); flagTok = RESULT; :};
	terminal Token varKwd_t 	::= /var/ in (keywds) {: RESULT = token(VAR,lexeme); :};
	terminal Token assertKwd_t 	::= /assert/ in (keywds) {: RESULT = token(ASSERT,lexeme); :}; 
	terminal Token recKwd_t::= /rec/ in (keywds) {: RESULT = token(REC,lexeme); flagTok = RESULT; :};
	terminal Token forwardKwd_t::= /forward/ in (keywds) {: RESULT = token(FORWARD,lexeme); :};
	terminal Token toKwd_t		::= /to/ in (keywds) {: RESULT = token(TO,lexeme); :};
	//terminal Token Kwd_t 	::= /fn/ in (keywds) {: RESULT = token(FN,lexeme); :};
	terminal Token requireKwd_t 	::= /require/ in (keywds) {: RESULT = token(REQUIRE,lexeme); :};
	terminal Token metadataKwd_t 	::= /metadata/ in (keywds) {: RESULT = token(METADATA,lexeme); :};
	terminal Token newKwd_t 	::= /new/ in (keywds) {: RESULT = token(NEW,lexeme); flagTok = RESULT; :};
 	terminal Token importKwd_t   ::= /import/ in (keywds) {: RESULT = token(IMPORT,lexeme); :};
 	terminal Token liftedKwd_t   ::= /lifted/ in (keywds) {: RESULT = token(LIFTED,lexeme); :};
 	terminal Token moduleKwd_t   ::= /module/ in (keywds) {: RESULT = token(MODULE,lexeme); :};
 	terminal Token comprisesKwd_t   ::= /comprises/ in (keywds) {: RESULT = token(COMPRISES,lexeme); :};
 	terminal Token extendsKwd_t   ::= /extends/ in (keywds) {: RESULT = token(EXTENDS,lexeme); :};
	//terminal Token ifKwd_t 	::= /if/ in (keywds);
 	//terminal Token thenKwd_t   ::= /then/ in (keywds);
 	//terminal Token elseKwd_t   ::= /else/ in (keywds);
 	terminal Token objtypeKwd_t   ::= /objtype/ in (keywds);
 	terminal Token effectKwd_t	::= /effect/ in (keywds) {: RESULT = token(EFFECT,lexeme); :};
 	
 	terminal Token resourceKwd_t    ::= /resource/ in (keywds) {: RESULT = token(RESOURCE,lexeme); :};
 	terminal Token asKwd_t ::= /as/ in (keywds) {: RESULT = token(AS,lexeme); :};
 	terminal Token instantiateKwd_t ::= /instantiate/ in (keywds) {: RESULT = token(INSTANTIATE,lexeme); :};

	terminal Token taggedKwd_t  ::= /tagged/  in (keywds) {: RESULT = token(TAGGED,lexeme); :};
    terminal Token matchKwd_t   ::= /match/   in (keywds) {: RESULT = token(MATCH,lexeme); flagTok = RESULT; :};
    terminal Token defaultKwd_t ::= /default/ in (keywds) {: RESULT = token(DEFLT,lexeme); :};
    terminal Token caseKwd_t ::= /case/ in (keywds);
    terminal Token ofKwd_t ::= /of/ in (keywds);
    terminal Token recurKwd_t ::= /recur/ in (keywds) {: RESULT = token(RECUR,lexeme); :};

 	terminal Token booleanLit_t ::= /true|false/ in (keywds) {: RESULT = token(BOOLEAN_LITERAL,lexeme); :};
	terminal Token floatingPoint_t ::= /([0]\.[0-9]*)|([1-9][0-9]*\.[0-9]+)|(\.[0-9]+)|([1-9][0-9]*\.)/ {: RESULT = token(FLOATING_POINT_LITERAL, lexeme); :};
 	terminal Token decimalInteger_t ::= /([1-9][0-9]*)|0/  {: RESULT = token(DECIMAL_LITERAL,lexeme); :};

	terminal Token tilde_t ::= /~/ {: RESULT = token(TILDE,lexeme); flagTok = RESULT; :};
	terminal Token plus_t ::= /\+/ {: RESULT = token(PLUS,lexeme); :};
	terminal Token dash_t ::= /-/ {: RESULT = token(DASH,lexeme); :};
	terminal Token mult_t ::= /\*/ {: RESULT = token(MULT,lexeme); :};
	terminal Token divide_t ::= /\// {: RESULT = token(DIVIDE,lexeme); :};
	terminal Token remainder_t ::= /%/ {: RESULT = token(MOD,lexeme); :};
	terminal Token equals_t ::= /=/ {:
		RESULT = token(EQUALS,lexeme);
		if (flagTok != null && flagTok.kind == DEF) // EQUALS cancels a DEF for the purposes of whether we are looking for a DSL on the next line
			flagTok = null;
	:};
	terminal Token equalsequals_t ::= /==/ {: RESULT = token(EQUALSEQUALS,lexeme); :};
	terminal Token openParen_t ::= /\(/ {: RESULT = token(LPAREN,lexeme); :};
 	terminal Token closeParen_t ::= /\)/ {: RESULT = token(RPAREN,lexeme); :};
 	terminal Token comma_t ::= /,/  {: RESULT = token(COMMA,lexeme); :};
 	terminal Token arrow_t ::= /=\>/  {:
        RESULT = token(EQARROW,lexeme);
        if (flagTok == null) {
            flagTok = RESULT;
        }
    :};
 	terminal Token tarrow_t ::= /-\>/  {: RESULT = token(TARROW,lexeme); :};
 	terminal Token dot_t ::= /\./ {: RESULT = token(DOT,lexeme); :};
 	terminal Token colon_t ::= /:/ {: RESULT = token(COLON,lexeme); :};
 	terminal Token pound_t ::= /#/ {: RESULT = token(POUND,lexeme); :};
 	terminal Token question_t ::= /?/ {: RESULT = token(QUESTION,lexeme); :};
 	terminal Token bar_t ::= /\|/ {: RESULT = token(BAR,lexeme); :};
 	terminal Token and_t ::= /&/ {: RESULT = token(AND,lexeme); :};
 	terminal Token gt_t ::= />/ {: RESULT = token(GT,lexeme); :};
 	terminal Token lt_t ::= /</ {: RESULT = token(LT,lexeme); :};
 	terminal Token ge_t ::= />=/ {: RESULT = token(GE,lexeme); :};
 	terminal Token le_t ::= /<=/ {: RESULT = token(LE,lexeme); :};
 	terminal Token notequals_t ::= /!=/ {: RESULT = token(NOTEQUALS,lexeme); :};

 	
    terminal Token oSquareBracket_t ::= /\[/ {: RESULT = token(LBRACK,lexeme); :};
    terminal Token cSquareBracket_t ::= /\]/ {: RESULT = token(RBRACK,lexeme); :};
    terminal Token booleanand_t ::= /&&/ {: RESULT = token(BOOLEANAND,lexeme); :};
    terminal Token booleanor_t ::= /\|\|/ {: RESULT = token(BOOLEANOR,lexeme); :};
    terminal Token booleannot_t ::= /!/ {: RESULT = token(BOOLEANNOT,lexeme); :};

	terminal Token shortString_t ::= /(('([^'\n\\]|\\.|\\O[0-7])*')|("([^"\n\\]|\\.|\\O[0-7])*"))|(('([^'\\]|\\.)*')|("([^"\\]|\\.)*"))/ {:
 		RESULT = token(STRING_LITERAL, replaceEscapeSequences(lexeme.substring(1,lexeme.length()-1)));
 	:};
 	
 	terminal Token character_t ::= /#(("\\?.")|('\\?.'))/ {:
 		RESULT = token(CHARACTER_LITERAL, replaceEscapeSequences(lexeme.substring(2,lexeme.length()-1)));
 	:};

 	terminal Token oCurly_t ::= /\{/ {: RESULT = token(LBRACE,lexeme); :};
 	terminal Token cCurly_t ::= /\}/ {: RESULT = token(RBRACE,lexeme); :};
 	terminal notCurly_t ::= /[^\{\}]*/ {: RESULT = lexeme; :};
    
 	terminal Token dslLine_t ::= /[^\n]*(\n|(\r\n))/ {: RESULT = token(DSLLINE,lexeme); flagTok = null; :};
 	
 	/* When is an indent a DSL?
 	 * Here's the abstract specification, implemented by the code below along with isDSLNext() and the code that sets flagTok:
 	 * - in general, all indents are DSLs.  The exceptions are:
 	 *   - when we are at a line of code with the type, datatype, new, or match keywords
 	 *   - when we are at a line of code with the def keyword and no = symbol after it
 	 * - note that case is implemented to allow DSLs but immediately translates them into other code (see WyvernParser.jj)
 	 */
 	// error if DSLNext but not indented further
 	// DSL if DSLNext and indented (unsets DSLNext, sets inDSL)
 	// DSL if inDSL and indented
 	// indent_t otherwise 
	disambiguate d1:(dsl_indent_t,indent_t)
	{:
		String currentIndent = indents.peek();
		if (lastIndent != null && !(currentIndent.equals(lastIndent.image)))
		    currentIndent = lastIndent.image;
		if (lexeme.length() > currentIndent.length() && lexeme.startsWith(currentIndent)) {
			// indented
			/*if (DSLNext != isDSLNext()) {
			    //throw new RuntimeException("unexpected difference");
			}*/
			if (/*DSLNext*/ isDSLNext() || inDSL) {
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
	disambiguate d2:(newline_t,fn_newline_t)
	{:
        if (isEQARROWlast) {
            return fn_newline_t;
        } else {
            return newline_t;
        }
	:};
	disambiguate d3:(newline_t,fn_newline_t,inewline_t)
	{:
        if (isEQARROWlast) {
            return fn_newline_t;
        } else if (lambdas.size()>0) {
            return inewline_t;
        } else {
            return newline_t;
        }
	:};
	disambiguate d4:(iindent_t,whitespace_t)
	{:
        if (ilineNext) {
            return iindent_t;
        } else {
            if (lambdas.size() == 0) {
                return whitespace_t;
            }
            String lambdaIndent = lambdas.get(lambdas.size()-1);
            if (lexeme.length() >= lambdaIndent.length() && lexeme.startsWith(lambdaIndent)) {
                return iindent_t;
            } else {
                return whitespace_t;
            }
        }
    :};
%lex}

%cf{
    non terminal innerdsl;
    non terminal inlinelit;
	non terminal List<Token> program;
	non terminal List<Token> lines;
	non terminal List<Token> ilines;
	non terminal List<Token> logicalLine;
	non terminal List<Token> ilogicalLine;
	non terminal List<Token> dslLine;
	non terminal List<Token> anyLineElement;
	non terminal List<Token> nonWSLineElement;
	non terminal List lineElementSequence;
	non terminal List ilineElementSequence;
	non terminal List<Token> parens;
	non terminal List<Token> parenContent;
	non terminal List<Token> parenContents;
	non terminal List<Token> optParenContents;
	non terminal Token operator;
	non terminal Token literal;
	non terminal Token keyw;
	non terminal List<Token> aLine;

	start with program;
	
	parenContent ::= anyLineElement:e {:
                        adjustEQARROW(e);
                        RESULT = e;
                    :}
	               | newline_t:t {: RESULT = LexerUtils.makeList(t); :}
	               | fn_newline_t:t ilines:l {:
                        RESULT = LexerUtils.makeList(t);
                        RESULT.addAll(l);
                        lambdas.pop();
                        // pop the old stack back on
                        indents = metaStack.pop();
                        RESULT.add(LexerUtils.makeToken(WyvernParserConstants.DEDENT, "DEDENT_end_of_lambda", t));
                    :};
	
	parenContents ::= parenContent:p {: RESULT = p; :}
	                | parenContents:ps parenContent:p {: RESULT = ps; ps.addAll(p); :};
	                
	optParenContents ::= parenContents:ps {: RESULT = ps; :}
	                   | {: RESULT = LexerUtils.emptyList(); :};
	
	parens ::= openParen_t:t1 optParenContents:list closeParen_t:t2 {:
	               RESULT = LexerUtils.makeList(t1);
	               RESULT.addAll(list);
	               RESULT.add(t2);
	           :}
	         | oSquareBracket_t:t1 optParenContents:list cSquareBracket_t:t2  {:
	               RESULT = LexerUtils.makeList(t1);
	               RESULT.addAll(list);
	               RESULT.add(t2);
	           :};
		
	keyw ::= classKwd_t:t {: RESULT = t; :}
	       | typeKwd_t:t {: RESULT = t; :}
	       | datatypeKwd_t:t {: RESULT = t; :}
	       | valKwd_t:t {: RESULT = t; :}
	       | defKwd_t:t {: RESULT = t; :}
	       | varKwd_t:t {: RESULT = t; :}
	       | assertKwd_t:t {: RESULT = t; :}
	       | recKwd_t:t {: RESULT = t; :}
	       | forwardKwd_t:t {: RESULT = t; :}
	       | toKwd_t:t {: RESULT = t; :}
	       | requireKwd_t:t {: RESULT = t; :}
	       | metadataKwd_t:t {: RESULT = t; :}
	       | newKwd_t:t {: RESULT = t; :}
	       | moduleKwd_t:t {: RESULT = t; :}
	       | comprisesKwd_t:t {: RESULT = t; :}
	       | extendsKwd_t:t {: RESULT = t; :}
	       | matchKwd_t:t {: RESULT = t; :}
	       | defaultKwd_t:t {: RESULT = t; :}
	       | taggedKwd_t:t {: RESULT = t; :}
	       | importKwd_t:t {: RESULT = t; :}
	       | liftedKwd_t:t {: RESULT = t; :}
	       | instantiateKwd_t:t {: RESULT = t; :}
	       | asKwd_t:t {: RESULT = t; :}
	       | resourceKwd_t:t {: RESULT = t; :}
	       | effectKwd_t:t {: RESULT = t; :}
	       | recurKwd_t:t {: RESULT = t; :}
	       ;

	literal ::= decimalInteger_t:t {: RESULT = t; :}
	          | floatingPoint_t:t {: RESULT = t; :}
	          | booleanLit_t:t {: RESULT = t; :}
	          | shortString_t:t {: RESULT = t; :}
	          | character_t:t {: RESULT = t; :}
	          | inlinelit:lit {: RESULT = token(DSL_LITERAL,(String)lit); :};
	
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
	           | ge_t:t {: RESULT = t; :}
	           | le_t:t {: RESULT = t; :}
	           | notequals_t:t {: RESULT = t; :}
               | booleanand_t:t {: RESULT = t; :}
               | booleanor_t:t {: RESULT = t; :}
               | booleannot_t:t {: RESULT = t; :}
	           ;

	anyLineElement ::= whitespace_t:n {: RESULT = LexerUtils.makeList(n); :}
	                 | nonWSLineElement:n {: RESULT = n; :};

	// a non-whitespace line element 
	nonWSLineElement ::= identifier_t:n {: RESULT = LexerUtils.makeList(n); :}
	                   | comment_t:n {: RESULT = LexerUtils.makeList(n); :}
	                   | multi_comment_t:n {: RESULT = LexerUtils.makeList(n); :}
	                   | continue_line_t:t  {: RESULT = LexerUtils.makeList(t); :}
	                   | operator:t {: RESULT = LexerUtils.makeList(t); :}
	                   | literal:t {: RESULT = LexerUtils.makeList(t); :}
	                   | keyw:t {: RESULT = LexerUtils.makeList(t); :}
	                   | parens:l {: RESULT = l; :};

    dslLine ::= dsl_indent_t:t dslLine_t:line {: RESULT = LexerUtils.makeList(t); RESULT.add(line); :};
	
    inlinelit ::= oCurly_t innerdsl:idsl cCurly_t {: RESULT = idsl; :};
    innerdsl ::= notCurly_t:str {: RESULT = str; :} | notCurly_t:str oCurly_t innerdsl:idsl cCurly_t innerdsl:stre {: RESULT = str + "{" + idsl + "}" + stre; :} | {: RESULT = ""; :};
    
	lineElementSequence ::= indent_t:n {: RESULT = LexerUtils.makeList(n); flagTok = null; lastIndent = n; :}
	                      | nonWSLineElement:n {:
	                            lastIndent = null;
	                            // handles lines that start without any indent
	                            if (inDSL)
	                                inDSL = false;
								if (DSLNext)
									throw new CopperParserException("Indicated DSL with ~ but then did not indent");
	                      		RESULT = n;
	                        :}
	                      | lineElementSequence:list anyLineElement:n {:
                                if (flagTok != null && flagTok.kind == EQARROW && list.size() > 0 && ((Token) list.get(list.size()-1)).kind != EQARROW && ((Token) list.get(list.size()-1)).kind != WHITESPACE) {
                                    flagTok = null;
                                }
                                list.addAll(n); RESULT = list;
                            :};
	
	logicalLine ::= lineElementSequence:list newline_t:n {:
						list.add(n);
						RESULT = LexerUtils.<WyvernParserConstants>adjustLogicalLine((LinkedList<Token>)list,
						                    virtualLocation.getFileName(), indents, WyvernParserConstants.class);
					    if (foundTilde) {
						    DSLNext = true;
						    foundTilde = false;
						}
					:}
				  | newline_t:n {: RESULT = LexerUtils.makeList(n); :}; // an empty line
				  
	aLine ::= dslLine:line {: RESULT = line; :}
	        | logicalLine:line  {: RESULT = line; :}; 
	          
	lines ::= logicalLine:line {: RESULT = line; :}
	        | lines:p aLine:line {: p.addAll(line); RESULT = p; :};

	ilineElementSequence ::= iindent_t:n {: RESULT = LexerUtils.makeList(n); flagTok = null; lastIndent = n; :}
	                      | ilineElementSequence:list anyLineElement:n {:
                                if (flagTok != null && flagTok.kind == EQARROW && list.size() > 0 && ((Token) list.get(list.size()-1)).kind != EQARROW && ((Token) list.get(list.size()-1)).kind != WHITESPACE) {
                                    flagTok = null;
                                }
                                list.addAll(n); RESULT = list;
                            :};
	ilogicalLine ::= ilineElementSequence:list inewline_t:n {:
						list.add(n);
						RESULT = LexerUtils.<WyvernParserConstants>adjustLogicalLine((LinkedList<Token>)list,
						                    virtualLocation.getFileName(), indents, WyvernParserConstants.class);
					    if (foundTilde) {
						    DSLNext = true;
						    foundTilde = false;
						}
					:}
				  | inewline_t:n {: RESULT = LexerUtils.makeList(n); :}; // an empty line
                  
    // not copying aLine or dslLine; update to support DSLs within indented lambdas
    ilines ::= ilogicalLine:line {: RESULT = line; :}
	        | ilines:p ilogicalLine:line {: p.addAll(line); RESULT = p; :};
            
	program ::= lines:p {:
	             	RESULT = p;
	             	Token t = ((LinkedList<Token>)p).getLast();
	             	RESULT.addAll(LexerUtils.<WyvernParserConstants>possibleDedentList(
	             	    t, virtualLocation.getFileName(), indents, WyvernParserConstants.class));
	           	:}
	          | lines:p lineElementSequence:list {:
	          		// handle the case of ending in an incomplete line
	          		RESULT = p;
	          		List<Token> adjustedList = LexerUtils.<WyvernParserConstants>adjustLogicalLine(
	          		    (LinkedList<Token>)list, virtualLocation.getFileName(), indents, WyvernParserConstants.class);
	          		RESULT.addAll(adjustedList);
	             	Token t = ((LinkedList<Token>)adjustedList).getLast();
	          		RESULT.addAll(LexerUtils.<WyvernParserConstants>possibleDedentList(
	          		    t, virtualLocation.getFileName(), indents, WyvernParserConstants.class));
	          	:}
	          | lineElementSequence:list {: RESULT = list; :} // a single-line program with no carriage return
	          | /* empty program */ {: RESULT = LexerUtils.emptyList(); :};
	
%cf}
