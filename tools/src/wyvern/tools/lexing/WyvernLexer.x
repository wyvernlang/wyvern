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

%%
%parser WyvernLexer

%aux{
	boolean foundTilde = false;
	boolean DSLNext = false;
	Stack<String> indents = new Stack<String>();
%aux}

%init{
	indents.push("");
%init}

%lex{
	class keywds;
    class specialNumbers;
    
    terminal whitespace_t ::= /[ \t]+/ {: RESULT = lexeme; :};
    terminal dsl_indent_t ::= /[ \t]+/ {: RESULT = lexeme; :};
    terminal indent_t ::= /[ \t]+/ {: RESULT = lexeme; :};

    terminal newline_t ::= /(\n|(\r\n))/ {: RESULT = lexeme; :};
    
    terminal continue_line_t ::= /\\(\n|(\r\n))/;

	terminal comment_t  ::= /\/\/([^\r\n])*/ {: RESULT = lexeme; :};
	terminal multi_comment_t  ::= /\/\*(.|\n|\r)*?\*\// {: RESULT = lexeme; :};
	
 	terminal String identifier_t ::= /[a-zA-Z_][a-zA-Z_0-9]*/ in (), < (keywds), > () {:
 		RESULT = lexeme;
 	:};

    terminal classKwd_t ::= /class/ in (keywds);
	terminal typeKwd_t 	::= /type/ in (keywds);
	terminal valKwd_t 	::= /val/ in (keywds);
	terminal defKwd_t 	::= /def/ in (keywds);
	terminal varKwd_t 	::= /var/ in (keywds);
	terminal fnKwd_t 	::= /fn/ in (keywds);
	terminal metadataKwd_t 	::= /metadata/ in (keywds);
	terminal newKwd_t 	::= /new/ in (keywds);
 	terminal importKwd_t   ::= /import/ in (keywds);
 	terminal moduleKwd_t   ::= /module/ in (keywds);
	terminal ifKwd_t 	::= /if/ in (keywds);
 	terminal thenKwd_t   ::= /then/ in (keywds);
 	terminal elseKwd_t   ::= /else/ in (keywds);
 	terminal objtypeKwd_t   ::= /objtype/ in (keywds);

	terminal taggedKwd_t  ::= /tagged/  in (keywds);
    terminal matchKwd_t   ::= /match/   in (keywds);
    terminal defaultKwd_t ::= /default/ in (keywds);
    terminal caseKwd_t ::= /case/ in (keywds);
    terminal ofKwd_t ::= /of/ in (keywds);
    terminal comprisesKwd_t ::= /comprises/ in (keywds);

 	terminal decimalInteger_t ::= /([1-9][0-9]*)|0/ {:
 		RESULT = Integer.parseInt(lexeme);
 	:};

	terminal tilde_t ::= /~/ ;
	terminal plus_t ::= /\+/ ;
	terminal dash_t ::= /-/ ;
	terminal mult_t ::= /\*/ ;
	terminal divide_t ::= /\// ;
	terminal equals_t ::= /=/ ;
	terminal equalsequals_t ::= /==/ ;
	terminal openParen_t ::= /\(/;
 	terminal closeParen_t ::= /\)/;
 	terminal comma_t ::= /,/ ;
 	terminal arrow_t ::= /=\>/ ;
 	terminal tarrow_t ::= /-\>/ ;
 	terminal dot_t ::= /\./ ;
 	terminal colon_t ::= /:/ ;
 	terminal pound_t ::= /#/ ;
 	terminal question_t ::= /?/ ;
 	terminal bar_t ::= /\|/ ;
 	terminal and_t ::= /&/ ;
 	terminal gt_t ::= />/ ;
 	terminal lt_t ::= /</ ;
    terminal oSquareBracket_t ::= /\[/;
    terminal cSquareBracket_t ::= /\]/;

 	terminal shortString_t ::= /(('([^'\n]|\\.|\\O[0-7])*')|("([^"\n]|\\.|\\O[0-7])*"))|(('([^']|\\.)*')|("([^"]|\\.)*"))/ {:
 		RESULT = lexeme.substring(1,lexeme.length()-1);
 	:};

 	terminal oCurly_t ::= /\{/;
 	terminal cCurly_t ::= /\}/;
 	terminal notCurly_t ::= /[^\{\}]*/ {: RESULT = lexeme; :};
 	
 	terminal dslLine_t ::= /[^\n]*(\n|(\r\n))/ {: RESULT = lexeme; :};
 	
 	
	disambiguate d1:(dsl_indent_t,indent_t)
	{:
		return DSLNext?dsl_indent_t:indent_t;
	:};
%lex}

%cf{
	non terminal LinkedList program;
	non terminal logicalLine;
	non terminal dslLine;
	non terminal anyLineElement;
	non terminal nonWSLineElement;
	non terminal LinkedList lineElementSequence;
	non terminal parens;
	non terminal parenContents;
	non terminal operator;
	non terminal aLine;

	start with program;
	
	parenContents ::= anyLineElement | newline_t |;
	
	parens ::= openParen_t parenContents closeParen_t
	         | oSquareBracket_t parenContents cSquareBracket_t;
	
	operator ::= tilde_t {: foundTilde = true; :}
	           | plus_t | dash_t | mult_t | divide_t | equals_t | comma_t
	           | arrow_t | tarrow_t | dot_t | colon_t | pound_t | question_t | bar_t | and_t
	           | gt_t | lt_t;
	           
	anyLineElement ::= whitespace_t:n {: RESULT = n; :}
	                 | nonWSLineElement:n {: RESULT = n; :};
	                 
	nonWSLineElement ::= identifier_t:n {: RESULT = n; :}
	                   | comment_t:n {: RESULT = n; :}
	                   | multi_comment_t:n {: RESULT = n; :}
	                   | continue_line_t
	                   | operator
	                   | parens;

    dslLine ::= dsl_indent_t dslLine_t:line {: RESULT = line; :};
	
	logicalLine ::= lineElementSequence:list newline_t:n
					{:	list.add(n);
					    if (foundTilde) {
						    DSLNext = true;
						    foundTilde = false;
						}
						RESULT = list;
					:}
				  | newline_t:n {: RESULT = "\n"; :};
				  
	lineElementSequence ::= indent_t:n {: LinkedList l = new LinkedList(); l.add(n); RESULT = l; :}
	                      | nonWSLineElement:n {: LinkedList l = new LinkedList(); l.add(n); RESULT = l; :}
	                      | lineElementSequence:list anyLineElement:n {: list.add(n); RESULT = list; :};
	
	aLine ::= dslLine:line {: RESULT = line; :}
	        | logicalLine:line  {: RESULT = line; :}; 
	          
//	program ::= dslLine_t:line {: LinkedList l = new LinkedList(); l.add(line); RESULT = l; :}
	program ::= logicalLine:line {: LinkedList l = new LinkedList(); l.add(line); RESULT = l; :}
	          | program:p aLine:line {: p.add(line); RESULT = p; :};
%cf}