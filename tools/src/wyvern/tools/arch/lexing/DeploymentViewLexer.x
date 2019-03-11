import static wyvern.tools.parsing.coreparser.arch.views.deployment.DeploymentViewParserConstants.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.ToolError;
import wyvern.tools.lexing.LexerUtils;
import wyvern.tools.parsing.coreparser.arch.views.deployment.DeploymentViewParserConstants;
import wyvern.tools.parsing.coreparser.Token;

%%
%parser DeploymentViewLexer

%aux{
    /*************************** LEXER  STATE ***************************/
    Stack<String> indents = new Stack<String>();

    /************************* HELPER FUNCTIONS *************************/

	/** Wraps the lexeme s in a Token, setting the begin line/column and kind appropriately
	 *  The current lexical location is used.
	 */
	Token token(int kind, String s) {
		// Copper starts counting columns at 0, but we want to follow convention and count columns starting at 1
		return LexerUtils.makeToken(kind, s, virtualLocation.getLine(), virtualLocation.getColumn()+1);
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
    class keywords;

    terminal Token deploymentKwd_t ::= /deployment/ in (keywords) {: RESULT = token(DEPLOYMENT, lexeme); :};
    terminal Token extendsKwd_t ::= /extends/ in (keywords) {: RESULT = token(EXTENDS, lexeme); :};

    terminal Token whitespace_t ::= /[ \t]+/ {: RESULT = token(WHITESPACE, lexeme); :};
    terminal Token indent_t ::= /[ \t]+/ {: RESULT = token(WHITESPACE, lexeme); :};
    terminal Token newline_t ::= /(\n|\r|(\r\n))/ {: RESULT = token(WHITESPACE, lexeme); :};
    terminal Token continue_line_t ::= /\\(\n|\r|(\r\n))/ {: RESULT = token(WHITESPACE, lexeme); :};

	terminal Token comment_t  ::= /\/\/([^\r\n])*/ {: RESULT = token(SINGLE_LINE_COMMENT, lexeme); :};
	terminal Token multi_comment_t  ::= /\/\*([^*]|\*[^/])*\*\// {: RESULT = token(MULTI_LINE_COMMENT, lexeme); :};

 	terminal Token identifier_t ::= /[a-zA-Z_][a-zA-Z_0-9]*/ in (), < (keywords), > () {:
        RESULT = token(IDENTIFIER, lexeme);
 	:};

    terminal Token dot_t ::= /\./ {: RESULT = token(DOT, lexeme); :};
    terminal Token equals_t ::= /=/ {: RESULT = token(EQUALS, lexeme); :};

	terminal Token stringLiteral_t ::=
	    /(('([^'\n\\]|\\.|\\O[0-7])*')|("([^"\n\\]|\\.|\\O[0-7])*"))|(('([^'\\]|\\.)*')|("([^"\\]|\\.)*"))/ {:
 		    RESULT = token(STRING_LITERAL, replaceEscapeSequences(lexeme.substring(1,lexeme.length()-1)));
 	    :};
%lex}

%cf{
    non terminal List<Token> program;
    non terminal List<Token> lines;
    non terminal List<Token> logicalLine;
    non terminal List<Token> lineElementSequence;
    non terminal List<Token> nonWSLineElement;
    non terminal Token operator;
    non terminal Token literal;
    non terminal Token keyword;
    non terminal List<Token> anyLineElement;

    start with program;

    program ::= lines:p {:
                    RESULT = p;
                    Token t = ((LinkedList<Token>)p).getLast();
                    RESULT.addAll(LexerUtils.<DeploymentViewParserConstants>possibleDedentList(
                        t, virtualLocation.getFileName(), indents, DeploymentViewParserConstants.class));
                :}
              | lines:p lineElementSequence:list {:
                    // handle the case of ending in an incomplete line
                    RESULT = p;
                    List<Token> adjustedList = LexerUtils.<DeploymentViewParserConstants>adjustLogicalLine(
                        (LinkedList<Token>)list, virtualLocation.getFileName(),
                         indents, DeploymentViewParserConstants.class);
                    RESULT.addAll(adjustedList);
                    Token t = ((LinkedList<Token>)adjustedList).getLast();
                    RESULT.addAll(LexerUtils.<DeploymentViewParserConstants>possibleDedentList(
                        t, virtualLocation.getFileName(), indents, DeploymentViewParserConstants.class));
                :}
              | lineElementSequence:list {: RESULT = list; :} // a single-line program with no carriage return
              | /* empty program */ {: RESULT = LexerUtils.emptyList(); :};

    lines ::= logicalLine:line {: RESULT = line; :}
            | lines:p logicalLine:line {: p.addAll(line); RESULT = p; :};

    logicalLine ::= lineElementSequence:list newline_t:n {:
                        list.add(n);
                        RESULT = LexerUtils.<DeploymentViewParserConstants>adjustLogicalLine((LinkedList<Token>)list,
                                            virtualLocation.getFileName(), indents,
                                            DeploymentViewParserConstants.class);
                    :}
                  | newline_t:n {: RESULT = LexerUtils.makeList(n); :}; // an empty line

    lineElementSequence ::= indent_t:n {: RESULT = LexerUtils.makeList(n); :}
                          | nonWSLineElement:n {:
                                // handles lines that start without any indent
                                RESULT = n;
                            :}
                          | lineElementSequence:list anyLineElement:n {: list.addAll(n); RESULT = list; :};

	// a non-whitespace line element
    nonWSLineElement ::= identifier_t:n {: RESULT = LexerUtils.makeList(n); :}
                       | comment_t:n {: RESULT = LexerUtils.makeList(n); :}
                       | multi_comment_t:n {: RESULT = LexerUtils.makeList(n); :}
                       | continue_line_t:t  {: RESULT = LexerUtils.makeList(t); :}
                       | operator:t {: RESULT = LexerUtils.makeList(t); :}
                       | literal:t {: RESULT = LexerUtils.makeList(t); :}
                       | keyword:t {: RESULT = LexerUtils.makeList(t); :};

    operator ::= dot_t:t {: RESULT = t; :}
               | equals_t:t {: RESULT = t; :};

    literal ::= stringLiteral_t:t {: RESULT = t; :};

    keyword ::= deploymentKwd_t:t {: RESULT = t; :}
              | extendsKwd_t:t {: RESULT = t; :};

    anyLineElement ::= whitespace_t:n {: RESULT = LexerUtils.makeList(n); :}
                     | nonWSLineElement:n {: RESULT = n; :};

%cf}
