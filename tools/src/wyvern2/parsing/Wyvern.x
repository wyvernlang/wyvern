package wyvern2.parsing;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import java.util.Arrays;

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
	ignore terminal Spaces_t ::= /[ \t]+|(\\\n)/;
    terminal Newline_t ::= /(\n[ \t]*)+/;

 	terminal identifier_t ::= /[a-zA-Z_][a-zA-Z_0-9]*/ in (), < (keywds), > () {:
 		RESULT = lexeme;
 	:};

    terminal classKwd_t ::= /class/ in (keywds);
	terminal typeKwd_t 	::= /type/ in (keywds);
	terminal valKwd_t 	::= /val/ in (keywds);
	terminal defKwd_t 	::= /def/ in (keywds);
	terminal varKwd_t 	::= /var/ in (keywds);
	terminal fnKwd_t 	::= /fn/ in (keywds);
	terminal metadataKwd_t 	::= /fn/ in (keywds);

 	terminal decimalInteger_t ::= /([1-9][0-9]*)|0/ {:
 		RESULT = Integer.parseInt(lexeme);
 	:};


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
 	terminal dsl_t ::= /\{.*?\}/ {: RESULT = lexeme; :};
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

    non terminal fc2;

	start with fc;

	fc2 ::= identifier_t;

	fc ::= p:pi {: RESULT = pi; :};

	p ::= e:ex {: RESULT = ex; :}
    	| d:de {: RESULT = de; :}
    	;

    d ::= prd:res {: RESULT = res; :}
    	| nrd:res {: RESULT = res; :}
    	;

    nrd ::= val:vd Newline_t p:re {: RESULT = new Sequence(Arrays.asList((TypedAST)vd,(TypedAST)re)); :}
    	|   var:vd Newline_t p:re {: RESULT = new Sequence(Arrays.asList((TypedAST)vd,(TypedAST)re)); :}
    	;

    prd ::= rd:vd Newline_t prd:re {: RESULT = new DeclSequence(Arrays.asList((TypedAST)vd,(TypedAST)re)); :}
    	  | rd:vd Newline_t p:re {: RESULT = new Sequence(Arrays.asList((TypedAST)vd,(TypedAST)re)); :}
    	  ;

    rd ::= class:res {: RESULT = res; :}
    	|  typedec:res {: RESULT = res; :}
    	|  def:res {: RESULT = res; :}
    	;

    class ::= classKwd_t identifier_t:id Indent_t objd:inner Dedent_t {: RESULT = new ClassDeclaration((String)id, null, null,
    	(inner instanceof DeclSequence)?(DeclSequence)inner : new DeclSequence((Declaration)inner), null); :}
    	|	  classKwd_t identifier_t:id {:RESULT = new ClassDeclaration((String)id, null, null, null, null); :}
    	;

    otypeasc ::= typeasc:ta {: RESULT = ta :}
    			| {: RESULT = null :}
    			;
    non terminal declbody;
    declbody ::= equals_t e:r {: RESULT = r; :} | Indent_t p:r Dedent_t {: RESULT = r; :};

    val ::= valKwd_t identifier_t:id otypeasc:ty declbody:body {: RESULT = new ValDeclaration((String)id, (Type)ty, (TypedAST)body, null); :};

    params ::= openParen_t iparams:ip closeParen_t {: RESULT = ip; :}
    		|  openParen_t closeParen_t {: RESULT = new LinkedList<NameBinding>(); :}
    		;

   	iparams ::= identifier_t typeasc:ta comma_t iparams :re
   			 |	identifier_t typeasc
   			 ;

    def ::= defKwd_t identifier_t params typeasc declbody
    	;

    var ::= varKwd_t identifier_t typeasc declbody
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

    non terminal AE;
    non terminal ME;

    e ::= AE:aer {:RESULT = aer; :};

    AE ::= AE:l plus_t ME:r {: RESULT = new Invocation((TypedAST)l,"+",(TypedAST)r,null); :}
    	|  AE:l dash_t ME:r {: RESULT = new Invocation((TypedAST)l,"-",(TypedAST)r,null); :}
    	|  ME:mer {:RESULT = mer;:};

    ME ::= ME:l mult_t term:r {: RESULT = new Invocation((TypedAST)l,"*",(TypedAST)r,null); :}
    	|  ME:l divide_t term:r {: RESULT = new Invocation((TypedAST)l,"/",(TypedAST)r,null); :}
    	|  term:ter {:RESULT = ter;:};

    term ::= identifier_t:id {: RESULT = new Variable(new NameBindingImpl((String)id, null), null); :}
    	|	 fnKwd_t identifier_t:id typeasc:t arrow_t openParen_t term:inner closeParen_t {: RESULT = new Fn(Arrays.asList(new NameBindingImpl((String)id, null)), (TypedAST)inner); :}
    	|	 openParen_t e:inner closeParen_t {: RESULT = inner; :}
    	|	 term:src tuple:tgt {: RESULT = new Application((TypedAST)src, (TypedAST)tgt, null); :}
    	|	 term:src dot_t identifier_t:op {: RESULT = new Invocation((TypedAST)src,(String)op, null, null); :}
    	//|	 term:src typeasc:as {: RESULT = new TypeAsc((Expr)src, (Type)as); :}
    	|	 inlinelit:lit {: RESULT = new DSLLit((String)lit); :}
    	|	 decimalInteger_t:res {: RESULT = new IntegerConstant((Integer)res); :}
    	;

    tuple ::= openParen_t it:res closeParen_t {:RESULT = res; :}
    		| openParen_t closeParen_t {: RESULT = UnitVal.getInstance(null); :}
    		;

   	it ::= e:first comma_t it:rest {: RESULT = new TupleObject((TypedAST)first,(TypedAST)rest,null); :}
   		|  e:el {: RESULT = new TupleObject(new TypedAST[] {(TypedAST)el}); :}
   		;


   	type ::= type:t1 tarrow_t type:t2 {: RESULT = new Arrow((Type)t1,(Type)t2); :}
   		|	 type:t1 mult_t type:t2 {: RESULT = new Tuple((Type)t1,(Type)t2); :}
   		|	 openParen_t type:ta closeParen_t {: RESULT = ta; :}
   		|	 identifier_t:id {: RESULT = new UnresolvedType((String)id); :}
   		;

   	typeasc ::= colon_t type:ty {: RESULT = ty; :};

   	inlinelit ::= dsl_t;

%cf}