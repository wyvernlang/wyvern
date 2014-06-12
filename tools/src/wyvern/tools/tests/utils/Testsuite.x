package wyvern.tools.tests.utils;
import java.util.LinkedList;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

%%
%parser TestSuiteParser

%aux{
	Integer parenLevel;
	Stack<Integer> depths;
	Pattern nlRegex;
	boolean nextDsl;
%aux}

%init{
	parenLevel = 0;
	nextDsl = false;
	depths = new Stack<Integer>();
	depths.push(0);
	nlRegex = Pattern.compile("(\r\n|\n)([\t ]*)");
%init}

%lex{
	class keywds;
	terminal test_t ::= /test/ in (keywds);
	terminal oBrack_t ::= /\[/;
	terminal cBrack_t ::= /\]/;
	terminal colon_t ::= /:/;

	terminal exVal_t ::= /[^:]*/ {: RESULT = lexeme; :};
	terminal exType_t ::= /[^\]]*/ {: RESULT = lexeme; :};
 	terminal identifier_t ::= /[a-zA-Z_][a-zA-Z_0-9]*/ in (), < (keywds), > () {:
 		RESULT = lexeme;
 	:};

	ignore terminal Spaces_t ::= /[ \t]+|(\\(\n|(\r\n)))/;
    terminal Newline_t ::= /((\n|(\r\n))[ \t]*)+/ {: :};
	terminal Indent_t ::= /(((\r\n)|\n)[ \t]*)+/
	{:
		//Need to determine new indentation depth and will treat all but the last "\n[\t ]*" as whitespace
		Matcher inputPattern = nlRegex.matcher(lexeme);
		String output = "";
		while(inputPattern.find()) {
			output = inputPattern.group(2);
		}
		int newDepth = output.length();
		depths.push(newDepth);
	:};

	terminal Dedent_t ::= /(((\r\n)|\n)[ \t]*)+/
	{:
		//Need to determine new indentation depth and will treat all but the last "\n[\t ]*" as whitespace
		Matcher inputPattern = nlRegex.matcher(lexeme);
		String output = "";
		while(inputPattern.find()) {
			output = inputPattern.group(2);
		}
		int newDepth = output.length();
		depths.pop();
		if(newDepth < depths.peek()) {
			pushToken(Terminals.Dedent_t,output);
		}
	:};

	terminal dslWhitespace_t ::= /((\r\n|\n)[ \t]*)+/ {: nextDsl = true; RESULT = "\n"+lexeme.substring(depths.peek()+1); :};
	terminal dslLine_t ::= /[^\n]+/ {: nextDsl = false; RESULT = lexeme.trim(); :};
	disambiguate dslWhitespace:(Dedent_t,dslWhitespace_t)
	{:
		Matcher inputPattern = nlRegex.matcher(lexeme);
		String output = "";
		while(inputPattern.find())
		{
			output = inputPattern.group(2);
		}
		int newDepth = output.length();
		if(newDepth < depths.peek()){
			return Dedent_t;
		} else {
			return dslWhitespace_t;
		}
	:};

	disambiguate spaces1:(Spaces_t,exVal_t){: return exVal_t; :};
	disambiguate spaces2:(Spaces_t,exType_t){: return exType_t; :};
	disambiguate dslLine1:(Spaces_t,dslLine_t)
	{:
		if (nextDsl) return dslLine_t;
		return Spaces_t;
	:};
%lex}

%cf{
	non terminal file;
	non terminal case;
	non terminal dslBlock;
	non terminal dslStart;
	non terminal dslInner;
	non terminal dslLine;
	start with file;
	file ::= case:test {: LinkedList<TestCase> res = new LinkedList<>(); res.add((TestCase)test); RESULT = res; :}
		   | case:test file:rest {: ((LinkedList<TestCase>)rest).addFirst((TestCase)test); RESULT = rest; :};
	case ::= test_t identifier_t:name
			oBrack_t exVal_t:eVal colon_t exType_t:eType cBrack_t dslBlock:code
			{: RESULT = new TestCase((String)name, (String)code, (String)eVal, (String)eType); :};

	dslBlock ::= Indent_t dslStart:dsl Dedent_t {: RESULT = dsl; :};
	dslStart ::= dslLine_t:s {: RESULT = s; :} | dslLine_t:st dslInner:i {: RESULT = (String)st + (String)i; :};
	dslInner ::= dslLine:i {: RESULT = i; :}| dslLine:i dslInner:n {: RESULT = (String)i + (String)n; :};
	dslLine ::= dslWhitespace_t:ws dslLine_t:ln {: RESULT = (String)ws + (String)ln; :};
%cf}