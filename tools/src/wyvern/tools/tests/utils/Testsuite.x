package wyvern.tools.tests.utils;
import java.util.LinkedList;

%%
%parser TestSuiteParser

%lex{
	class keywds;
	terminal test_t ::= /test/ in (keywds);
	terminal oBrack_t ::= /\[/;
	terminal cBrack_t ::= /\]/;
	terminal oCurly_t ::= /\{:/;
	terminal cCurly_t ::= /:\}/;
	terminal colon_t ::= /:/;

	terminal exVal_t ::= /[^:]*/ {: RESULT = lexeme; :};
	terminal exType_t ::= /[^\]]*/ {: RESULT = lexeme; :};
	terminal code_t ::= /[^(:\})]*/ {: RESULT = lexeme; :};
 	terminal identifier_t ::= /[a-zA-Z_][a-zA-Z_0-9]*/ in (), < (keywds), > () {:
 		RESULT = lexeme;
 	:};

	disambiguate newline1:(Newline_t,exVal_t){: return exVal_t; :};
	disambiguate newline2:(Newline_t,exType_t){: return exType_t; :};
	disambiguate newline3:(Newline_t,code_t){: return code_t; :};

	disambiguate spaces1:(Spaces_t,exVal_t){: return exVal_t; :};
	disambiguate spaces2:(Spaces_t,exType_t){: return exType_t; :};
	disambiguate spaces3:(Spaces_t,code_t){: return code_t; :};

	ignore terminal Newline_t ::= /\n|(\r\n)/;
	ignore terminal Spaces_t ::= /[ \t]+|(\\\n)/;
%lex}

%cf{
	non terminal file;
	non terminal case;
	start with file;
	file ::= case:test {: LinkedList<TestCase> res = new LinkedList<>(); res.add((TestCase)test); RESULT = res; :}
		   | case:test file:rest {: ((LinkedList<TestCase>)rest).addFirst((TestCase)test); RESULT = rest; :};
	case ::= test_t identifier_t:name
			oBrack_t exVal_t:eVal colon_t exType_t:eType cBrack_t oCurly_t code_t:code cCurly_t
			{: RESULT = new TestCase((String)name, (String)code, (String)eVal, (String)eType); :};
%cf}