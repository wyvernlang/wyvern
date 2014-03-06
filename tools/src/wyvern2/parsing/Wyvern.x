package wyvern2.parsing;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

%%
%parser Wyvern

%aux{
	Integer parenLevel;
	Stack<Integer> depths;
	Pattern nlRegex;
%aux}

%init{
	parenLevel = 0;
	depths = new Stack<Integer>();
	depths.push(0);
	nlRegex = Pattern.compile("\n[\t ]*");
%init}

%lex{
	disambiguate ignoredNewline1:(Newline_t,ignoredNewline)
	{:
		if(parenLevel > 0){
			return ignoredNewline;
		}
		else
		{
			return Newline_t;
		}
	:};

	disambiguate ignoredNewline2:(Newline_t,ignoredNewline, Dedent_t)
	{:
		if(parenLevel > 0){
			return ignoredNewline;
		}
		//Given the lexeme of the terminals, need to treat all but the last "\n[\t ]*" as whitespace
		Matcher inputPattern = nlRegex.matcher(lexeme);
		String output = "";
		while(inputPattern.find())
		{
			output = inputPattern.group();
		}
		int newDepth = output.length() - 1;
		if(newDepth < depths.peek()){
			return Dedent_t;
		} else {
			return Newline_t;
		}
	:};

	disambiguate ignoredNewline3:(ignoredNewline, Dedent_t)
	{:
		if(parenLevel > 0){
			return ignoredNewline;
		}
		return Dedent_t;
	:};

	disambiguate ignoredNewline4:(ignoredNewline, Indent_t)
	{:
		if(parenLevel > 0){
			return ignoredNewline;
		}
		return Indent_t;
	:};


	disambiguate ignoredNewline5:(ignoredNewline, Newline_t, DedentRepair_t)
	{:
		if(parenLevel > 0){
			return ignoredNewline;
		}
		//Given the lexeme of the terminals, need to treat all but the last "\n[\t ]*" as whitespace
		Matcher inputPattern = nlRegex.matcher(lexeme);
		String output = "";
		while(inputPattern.find()) {
			output = inputPattern.group();
		}
		int newDepth = output.length() - 1;
		if(newDepth < depths.peek()) {
			return DedentRepair_t;
		} else {
			return Newline_t;
		}
	:};

	disambiguate ignoredNewline6:(Indent_t, Newline_t, ignoredNewline) 
	{:
		if(parenLevel > 0){
			return ignoredNewline;
		}

		Matcher inputPattern = nlRegex.matcher(lexeme);
		String output = "";
		while(inputPattern.find())
		{
			output = inputPattern.group();
		}
		int newDepth = output.length() - 1;
		if(newDepth > depths.peek()){
			return Indent_t;
		} else {
			return Newline_t;
		}
	:};

	disambiguate ignoredNewline7:(Dedent_t,Indent_t,Newline_t,ignoredNewline)
	{:
		if (parenLevel > 0)
			return ignoredNewline;

		Matcher inputPattern = nlRegex.matcher(lexeme);
		String output = "";
		while(inputPattern.find())
		{
			output = inputPattern.group();
		}
		int newDepth = output.length() - 1;
		if(newDepth > depths.peek()){
			return Indent_t;
		} else if (newDepth < depths.peek()) {
			return Dedent_t;
		} else {
			return Newline_t;
		}
	:};




	class keywds;
    class specialNumbers;

	terminal Indent_t ::= /(\n[ \t]*)+/
	{:
		//Need to determine new indentation depth and will treat all but the last "\n[\t ]*" as whitespace
		Matcher inputPattern = nlRegex.matcher(lexeme);
		String output = "";
		while(inputPattern.find()) {
			output = inputPattern.group();
		}
		int newDepth = output.length() - 1;
		depths.push(newDepth);
	:};

	terminal Dedent_t ::= /(\n[ \t]*)+/
	{:
		//Need to determine new indentation depth and will treat all but the last "\n[\t ]*" as whitespace
		Matcher inputPattern = nlRegex.matcher(lexeme);
		String output = "";
		while(inputPattern.find()) {
			output = inputPattern.group();
		}
		int newDepth = output.length() - 1;
		depths.pop();
		if(newDepth < depths.peek()) {
			pushToken(Terminals.Dedent_t,output);
		}
	:};

	terminal DedentRepair_t ::= /(\n[ \t]*)+/
	{:
		pushToken(Terminals.Dedent_t,lexeme);
	:};



	ignore terminal comment_t  ::= /\/\/([^\r\n])*/;
	ignore terminal multi_comment_t  ::= /\/\*(.|\n|\r)*?\*\//;
	ignore terminal ignoredNewline ::= /(\n[ \t]*)+/;
    terminal Newline_t ::= /(\n[ \t]*)+/;

    terminal classKwd_t ::= /class/ in (keywds);
	terminal typeKwd_t 	::= /type/ in (keywds);
	terminal valKwd_t 	::= /val/ in (keywds);
	terminal defKwd_t 	::= /def/ in (keywds);
	terminal varKwd_t 	::= /var/ in (keywds);
	terminal fnKwd_t 	::= /fn/ in (keywds);
	terminal metadataKwd_t 	::= /fn/ in (keywds);

 	terminal decimalInteger_t ::= /([1-9][0-9]*)|0/ ;

 	terminal identifier_t ::= /[a-zA-Z_][a-zA-Z_0-9]*/ in (), < (keywds), > () ;

	terminal tilde_t ::= /~/ ;
	terminal plus_t ::= /\+/ ;
	terminal dash_t ::= /-/ ;
	terminal mult_t ::= /\*/ ;
	terminal divide_t ::= /\// ;
	terminal equals_t ::= /=/ ;
	terminal openParen_t ::= /\(/  {:  parenLevel++; :};
 	terminal closeParen_t ::= /\)/ {:  parenLevel--; :};
 	terminal comma_t ::= /,/ ;
 	terminal arrow_t ::= /=\>/ ;
 	terminal tarrow_t ::= /-\>/ ;
 	terminal dot_t ::= /\./ ;
 	terminal colon_t ::= /:/ ;


 	terminal shortString_t ::= /(('([^'\n]|\\.|\\O[0-7])*')|("([^"\n]|\\.|\\O[0-7])*"))|(('([^']|\\.)*')|("([^"]|\\.)*"))/ ;
 	terminal dsl_t ::= /\{.*?\}/;
%lex}

%cf{
	non terminal p;
	non terminal type;
	non terminal typeasc;
	non terminal d;
	non terminal nrd;
	non terminal prd;
	non terminal rd;
	non terminal val;
	non terminal def;
	non terminal var;
	non terminal params;
	non terminal iparams;
	non terminal objd;
	non terminal objid;
	non terminal objcd;
	non terminal tdef;
	non terminal metadata;
	non terminal e;
	non terminal term;
	non terminal tuple;
	non terminal it;
	non terminal class;
	non terminal fc;
	non terminal typedec;
	non terminal otypeasc;
	non terminal objrd;
	non terminal typed;
	non terminal inlinelit;

   	precedence right tarrow_t;
    precedence left colon_t;
    precedence left openParen_t;
    precedence left dot_t;
    precedence left plus_t, dash_t;
    precedence left mult_t, divide_t;

	start with fc;

	fc ::= p;

	p ::= e
    	| d
    	;

    d ::= prd 
    	| nrd
    	;

    nrd ::= val Newline_t p
    	|   var Newline_t p
    	;

    prd ::= rd Newline_t prd
    	  | rd Newline_t p
    	  ;

    rd ::= class
    	|  typedec
    	|  def
    	;

    class ::= classKwd_t identifier_t Indent_t objd Dedent_t
    	|	  classKwd_t identifier_t
    	;

    otypeasc ::= typeasc
    			|
    			;

    val ::= valKwd_t identifier_t otypeasc equals_t e
    	| 	valKwd_t identifier_t otypeasc Indent_t p Dedent_t
    	;

    params ::= openParen_t iparams closeParen_t
    		|  openParen_t closeParen_t
    		;

   	iparams ::= identifier_t typeasc comma_t iparams
   			 |	identifier_t typeasc
   			 ;

    def ::= defKwd_t identifier_t params typeasc equals_t e
    	|	defKwd_t identifier_t params typeasc Indent_t p Dedent_t
    	;

    var ::= varKwd_t identifier_t typeasc equals_t e
    	|	varKwd_t identifier_t typeasc Indent_t p Dedent_t
    	;

    objd ::= objcd Newline_t objd
    	|	 objrd
    	|
    	;

    objrd ::= objid Newline_t objrd
    	|	  objid
    	|
    	;

    objcd ::= classKwd_t def;
    objid ::= val
    	|	  var
    	|	  def
    	;


    typedec ::= typeKwd_t identifier_t Indent_t typed Dedent_t
    	|	    typeKwd_t identifier_t
    	;

    typed ::= tdef Newline_t typed
    	   | tdef
    	   | metadata
    	   ;

    tdef ::= defKwd_t identifier_t params typeasc;

    metadata ::= metadataKwd_t equals_t e;

    e ::= term plus_t term
    	| term dash_t term
    	| term mult_t term
    	| term divide_t term
    	| term
    	;

    term ::= identifier_t
    	|	 fnKwd_t identifier_t typeasc arrow_t e
    	|	 term tuple
    	|	 term dot_t identifier_t
    	|	 term typeasc
    	|	 inlinelit
    	|	 decimalInteger_t
    	;

    tuple ::= openParen_t it closeParen_t
    		| openParen_t closeParen_t
    		;

   	it ::= e comma_t it
   		|  e 
   		;


   	type ::= type tarrow_t type
   		|	 type mult_t type
   		|	 openParen_t type closeParen_t
   		|	 identifier_t
   		;

   	typeasc ::= colon_t type;

   	inlinelit ::= dsl_t;

%cf}