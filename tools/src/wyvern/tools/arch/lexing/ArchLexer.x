import static wyvern.tools.parsing.coreparser.arch.ArchParserConstants.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import wyvern.tools.lexing.LexerUtils;
import wyvern.tools.parsing.coreparser.arch.ArchParserConstants;
import wyvern.tools.parsing.coreparser.Token;

%%
%parser ArchLexer

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

%aux}

%init{
    // start with the baseline indentation level
    indents.push("");
%init}

%lex{
    class keywords;

    terminal Token componentKwd_t ::= /component/ in (keywords) {: RESULT = token(COMPONENT, lexeme); :};
    terminal Token externalKwd_t ::= /external/ in (keywords) {: RESULT = token(EXTERNAL, lexeme); :};
    terminal Token portKwd_t ::= /port/ in (keywords) {: RESULT = token(PORT, lexeme); :};
    terminal Token providesKwd_t ::= /provides/ in (keywords) {: RESULT = token(PROVIDES, lexeme); :};
    terminal Token requiresKwd_t ::= /requires/ in (keywords) {: RESULT = token(REQUIRES, lexeme); :};
    terminal Token connectorKwd_t ::= /connector/ in (keywords) {: RESULT = token(CONNECTOR, lexeme); :};
    terminal Token valKwd_t ::= /val/ in (keywords) {: RESULT = token(VAL, lexeme); :};
    terminal Token architectureKwd_t ::= /architecture/ in (keywords) {: RESULT = token(ARCHITECTURE, lexeme); :};
    terminal Token componentsKwd_t ::= /components/ in (keywords) {: RESULT = token(COMPONENTS, lexeme); :};
    terminal Token connectorsKwd_t ::= /connectors/ in (keywords) {: RESULT = token(CONNECTORS, lexeme); :};
    terminal Token attachmentsKwd_t ::= /attachments/ in (keywords) {: RESULT = token(ATTACHMENTS, lexeme); :};
    terminal Token connectKwd_t ::= /connect/ in (keywords) {: RESULT = token(CONNECT, lexeme); :};
    terminal Token andKwd_t ::= /and/ in (keywords) {: RESULT = token(AND, lexeme); :};
    terminal Token withKwd_t ::= /with/ in (keywords) {: RESULT = token(WITH, lexeme); :};
    terminal Token entryPointsKwd_t ::= /entryPoints/ in (keywords) {: RESULT = token(ENTRYPOINTS, lexeme); :};
    terminal Token bindingsKwd_t ::= /bindings/ in (keywords) {: RESULT = token(BINDINGS, lexeme); :};
    terminal Token isKwd_t ::= /is/ in (keywords) {: RESULT = token(IS, lexeme); :};

    terminal Token whitespace_t ::= /[ \t]+/ {: RESULT = token(WHITESPACE, lexeme); :};
    terminal Token indent_t ::= /[ \t]+/ {: RESULT = token(WHITESPACE, lexeme); :};
    terminal Token newline_t ::= /(\n|\r|(\r\n))/ {: RESULT = token(WHITESPACE, lexeme); :};
    terminal Token continue_line_t ::= /\\(\n|\r|(\r\n))/ {: RESULT = token(WHITESPACE, lexeme); :};

	terminal Token comment_t  ::= /\/\/([^\r\n])*/ {: RESULT = token(SINGLE_LINE_COMMENT, lexeme); :};
	terminal Token multi_comment_t  ::= /\/\*([^*]|\*[^/])*\*\// {: RESULT = token(MULTI_LINE_COMMENT, lexeme); :};

 	terminal Token identifier_t ::= /[a-zA-Z_][a-zA-Z_0-9]*/ in (), < (keywords), > () {:
        RESULT = token(IDENTIFIER, lexeme);
 	:};

    terminal Token colon_t ::= /:/ {: RESULT = token(COLON, lexeme); :};
    terminal Token dot_t ::= /\./ {: RESULT = token(DOT, lexeme); :};
    terminal Token comma_t ::= /,/ {: RESULT = token(COMMA, lexeme); :};
%lex}

%cf{
    non terminal List<Token> program;
    non terminal List<Token> lines;
    non terminal List<Token> logicalLine;
    non terminal List<Token> lineElementSequence;
    non terminal List<Token> nonWSLineElement;
    non terminal Token operator;
    non terminal Token keyword;
    non terminal List<Token> anyLineElement;

    start with program;

    program ::= lines:p {:
                    RESULT = p;
                    Token t = ((LinkedList<Token>)p).getLast();
                    RESULT.addAll(LexerUtils.<ArchParserConstants>possibleDedentList(
                        t, virtualLocation.getFileName(), indents, ArchParserConstants.class));
                :}
              | lines:p lineElementSequence:list {:
                    // handle the case of ending in an incomplete line
                    RESULT = p;
                    List<Token> adjustedList = LexerUtils.<ArchParserConstants>adjustLogicalLine(
                        (LinkedList<Token>)list, virtualLocation.getFileName(), indents, ArchParserConstants.class);
                    RESULT.addAll(adjustedList);
                    Token t = ((LinkedList<Token>)adjustedList).getLast();
                    RESULT.addAll(LexerUtils.<ArchParserConstants>possibleDedentList(
                        t, virtualLocation.getFileName(), indents, ArchParserConstants.class));
                :}
              | lineElementSequence:list {: RESULT = list; :} // a single-line program with no carriage return
              | /* empty program */ {: RESULT = LexerUtils.emptyList(); :};

    lines ::= logicalLine:line {: RESULT = line; :}
            | lines:p logicalLine:line {: p.addAll(line); RESULT = p; :};

    logicalLine ::= lineElementSequence:list newline_t:n {:
                        list.add(n);
                        RESULT = LexerUtils.<ArchParserConstants>adjustLogicalLine((LinkedList<Token>)list,
                                            virtualLocation.getFileName(), indents, ArchParserConstants.class);
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
                       | keyword:t {: RESULT = LexerUtils.makeList(t); :};

    operator ::= colon_t:t {: RESULT = t; :}
               | dot_t:t {: RESULT = t; :}
	       | comma_t:t {: RESULT = t; :};

    keyword ::= componentKwd_t:t {: RESULT = t; :}
              | externalKwd_t:t {: RESULT = t; :}
              | portKwd_t:t {: RESULT = t; :}
              | providesKwd_t:t {: RESULT = t; :}
              | requiresKwd_t:t {: RESULT = t; :}
              | connectorKwd_t:t {: RESULT = t; :}
              | valKwd_t:t {: RESULT = t; :}
              | architectureKwd_t:t {: RESULT = t; :}
              | componentsKwd_t:t {: RESULT = t; :}
              | connectorsKwd_t:t {: RESULT = t; :}
              | attachmentsKwd_t:t {: RESULT = t; :}
              | connectKwd_t:t {: RESULT = t; :}
              | andKwd_t:t {: RESULT = t; :}
              | withKwd_t:t {: RESULT = t; :}
              | entryPointsKwd_t:t {: RESULT = t; :}
              | bindingsKwd_t:t {: RESULT = t; :}
              | isKwd_t:t {: RESULT = t; :};

    anyLineElement ::= whitespace_t:n {: RESULT = LexerUtils.makeList(n); :}
                     | nonWSLineElement:n {: RESULT = n; :};

%cf}
