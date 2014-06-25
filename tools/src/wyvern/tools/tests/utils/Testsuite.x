package wyvern.tools.tests.utils;
import java.util.LinkedList;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import wyvern.tools.util.Pair;

%%
%parser TestSuiteParser

%aux{
	Integer parenLevel;
	Stack<Integer> depths;
	Pattern nlRegex;
	Pattern wsRegex;
	boolean nextDsl;
	private String getLastMatch(Pattern patt, String str, int groupN) {
		String output = "";
		Matcher input = patt.matcher(str);
		while (input.find())
			output = input.group(groupN);
		return output;
	}
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
	terminal module_t ::= /module/ in (keywds);
	terminal file_t ::= /file/ in (keywds);
	terminal oBrack_t ::= /\[/;
	terminal cBrack_t ::= /\]/;
	terminal colon_t ::= /:/;

	terminal exVal_t ::= /[^:]*/ {: RESULT = lexeme; :};
	terminal exType_t ::= /[^\]]*/ {: RESULT = lexeme; :};
 	terminal identifier_t ::= /[a-zA-Z_][a-zA-Z_0-9]*/ in (), < (keywds), > () {:
 		RESULT = lexeme;
 	:};

	ignore terminal Spaces_t ::= /[ \t]+|(\\(\n|(\r\n)))/;
    terminal Newline_t ::= /((\n|(\r\n))[ \t]*)+/ {: System.out.println("nl"); :};
	terminal Indent_t ::= /(((\r\n)|\n)[ \t]*)+/
	{:
		//Need to determine new indentation depth and will treat all but the last "\n[\t ]*" as whitespace
		String output = getLastMatch(nlRegex, lexeme, 2);
		depths.push(output.length());
	:};

	terminal Dedent_t ::= /(((\r\n)|\n)[ \t]*)+/
	{:
		String inp = lexeme;

		if (!inp.startsWith("\n"))
			inp = "\n" + inp;

		//Need to determine new indentation depth and will treat all but the last "\n[\t ]*" as whitespace
		String output = getLastMatch(nlRegex, inp, 2);
		int newDepth = output.length();
		depths.pop();
		if(newDepth < depths.peek()) {
			pushToken(Terminals.Dedent_t,output);
		}
	:};
	disambiguate dedent2:(Newline_t, Dedent_t)
	{:
		//Given the lexeme of the terminals, need to treat all but the last "\n[\t ]*" as whitespace
		String output = getLastMatch(nlRegex, lexeme, 2);
		int newDepth = output.length();
		if(newDepth < depths.peek()){
			return Dedent_t;
		} else {
			return Newline_t;
		}
	:};

	terminal dslWhitespace_t ::= /((\r\n|\n)[ \t]*)+/ {:
		nextDsl = true;
		String newWs = getLastMatch(nlRegex, lexeme, 2).substring(depths.peek());
		RESULT = "\n"+newWs;
	:};
	terminal dslLine_t ::= /[^\n]+/ {: nextDsl = false; RESULT = lexeme.trim(); :};
	disambiguate dslWhitespace:(Dedent_t,dslWhitespace_t)
	{:
		String output = getLastMatch(nlRegex, lexeme, 2);
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
	non terminal result;
	non terminal moduleTests;
	non terminal moduleTest;

	start with file;
	file ::= case:test {: LinkedList<TestCase> res = new LinkedList<>(); res.add((TestCase)test); RESULT = res; :}
		   | case:test file:rest {: ((LinkedList<TestCase>)rest).addFirst((TestCase)test); RESULT = rest; :};
	case ::= test_t identifier_t:name result:res dslBlock:code
			{: RESULT = new SingleTestCase((String)name, (String)code, ((Pair<String,String>)res).first, ((Pair<String,String>)res).second); :}
		|	 module_t test_t identifier_t:name result:res Indent_t moduleTests:tests Dedent_t
				{: RESULT = new ModuleTestCase((String)name, (Pair<String,String>)res, (LinkedList<Pair<String,String>>)tests); :};

	moduleTests ::= moduleTests:res moduleTest:test {: ((LinkedList<Pair<String, String>>)res).addLast((Pair<String,String>)test); RESULT = res; :}
				|   moduleTest:test {: LinkedList<Pair<String,String>> res = new LinkedList<>(); res.add((Pair<String,String>)test); RESULT = res; :};

	moduleTest ::= file_t identifier_t:name dslBlock:code {: RESULT = new Pair<String, String>((String)name, (String)code); :};

	result ::= oBrack_t exVal_t:eVal colon_t exType_t:eType cBrack_t {: RESULT = new Pair<String,String>((String)eVal, (String)eType); :};

	dslBlock ::= Indent_t dslStart:dsl Dedent_t {: RESULT = dsl; :};
	dslStart ::= dslLine_t:s {: RESULT = s; :} | dslLine_t:st dslInner:i {: RESULT = (String)st + (String)i; :};
	dslInner ::= dslLine:i {: RESULT = i; :}| dslLine:i dslInner:n {: RESULT = (String)i + (String)n; :};
	dslLine ::= dslWhitespace_t:ws dslLine_t:ln {: RESULT = (String)ws + (String)ln; :};
%cf}