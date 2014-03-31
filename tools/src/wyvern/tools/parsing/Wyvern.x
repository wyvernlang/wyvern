package wyvern.tools.parsing;
import java.util.Stack;
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
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.HashMap;
import java.util.*;
import wyvern.tools.errors.FileLocation;

%%
%parser Wyvern

%aux{
	Integer parenLevel, cl;
	Stack<Integer> depths;
	Pattern nlRegex;
	boolean nextDsl;
%aux}

%init{
	parenLevel = 0;
	cl = 0;
	nextDsl = false;
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

	disambiguate ignoredNewline8:(Dedent_t,Indent_t,ignoredNewline)
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
		} else {
			return Dedent_t;
		}
	:};

	disambiguate curlyDsl1:(Spaces_t,notCurly_t)
	{:
		if (cl > 0) return notCurly_t;
		return Spaces_t;
	:};
	disambiguate curlyDsl2:(comment_t,notCurly_t)
	{:
		if (cl > 0) return notCurly_t;
		return comment_t;
	:};
	disambiguate curlyDsl3:(multi_comment_t,notCurly_t)
	{:
		if (cl > 0) return notCurly_t;
		return multi_comment_t;
	:};
	disambiguate curlyDsl4:(ignoredNewline,notCurly_t)
	{:
		if (cl > 0) return notCurly_t;
		return ignoredNewline;
	:};
	disambiguate dslWhitespace1:(dslWhitespace_t,ignoredNewline)
	{:
		return dslWhitespace_t;
	:};
	disambiguate dslWhitespace2:(Dedent_t,dslWhitespace_t,ignoredNewline)
	{:
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
			return dslWhitespace_t;
		}
	:};

	disambiguate dslLine1:(comment_t,dslLine_t)
	{:
		if (nextDsl) return dslLine_t;
		return comment_t;
	:};
	disambiguate dslLine2:(dslLine_t,multi_comment_t)
	{:
		if (nextDsl) return dslLine_t;
		return dslLine_t;
	:};
	disambiguate dslLine3:(Spaces_t,dslLine_t)
	{:
		if (nextDsl) return dslLine_t;
		return Spaces_t;
	:};

	disambiguate signal:(dslSignal_t,newSignal_t)
	{:
		//Should never be used.
		throw new RuntimeException();
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
    terminal Newline_t ::= /(\n[ \t]*)+/ {: :};

 	terminal identifier_t ::= /[a-zA-Z_][a-zA-Z_0-9]*/ in (), < (keywds), > () {:
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


 	terminal shortString_t ::= /(('([^'\n]|\\.|\\O[0-7])*')|("([^"\n]|\\.|\\O[0-7])*"))|(('([^']|\\.)*')|("([^"]|\\.)*"))/ {:
 		RESULT = lexeme.substring(1,lexeme.length()-1);
 	:};

 	terminal oCurly_t ::= /\{/ {: cl++; :};
 	terminal cCurly_t ::= /\}/ {: cl--; :};
 	terminal notCurly_t ::= /[^\{\}]*/ {: RESULT = lexeme; :};

 	terminal dslWhitespace_t ::= /(\n[ \t]*)+/ {: nextDsl = true; RESULT = "\n"+lexeme.substring(depths.peek()+1); :};
 	terminal dslLine_t ::= /[^\n]+/ {: nextDsl = false; RESULT = lexeme; :};

 	terminal newSignal_t ::= //;
 	terminal dslSignal_t ::= //;
 	terminal caseSignal_t ::= //;
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
   	non terminal dslBlock;
   	non terminal dslInner;
   	non terminal dsle;
   	non terminal newBlock;
   	non terminal dslce;
   	non terminal newCBlock;
   	non terminal obljid;
   	non terminal objcld;

   	precedence right tarrow_t;
   	precedence left Dedent_t;
    precedence left colon_t;
    precedence left openParen_t;
    precedence left dot_t;
    precedence left plus_t, dash_t;
    precedence left mult_t, divide_t;

    non terminal fc2;

	start with fc;

	fc2 ::= identifier_t;

	fc ::= p:pi {: RESULT = pi; :};

	p ::= dslce:ex {: RESULT = ex; :}
    	| d:de {: RESULT = de; :}
    	;

	non terminal pnrd;

    d ::= prd:res pnrd:after {: RESULT = new Sequence(Arrays.asList((TypedAST)res,(TypedAST)after)); :}
    	| prd:res {: RESULT = res; :}
    	| nrd:res {: RESULT = res; :}
    	;

    nrd ::= val:vd p:re {: RESULT = new Sequence(Arrays.asList((TypedAST)vd,(TypedAST)re)); :}
    	|   var:vd p:re {: RESULT = new Sequence(Arrays.asList((TypedAST)vd,(TypedAST)re)); :}
    	;


    prd ::= rd:vd {: RESULT = vd; :}
    	  | rd:vd prd:re {: RESULT = DeclSequence.simplify(new DeclSequence(Arrays.asList((TypedAST)vd,(TypedAST)re))); :}
    	  ;

    pnrd ::= e:ex {: RESULT = ex; :}| nrd:nr {: RESULT = nr; :};

    rd ::= class:res {: RESULT = res; :}
    	|  typedec:res {: RESULT = res; :}
    	|  def:res {: RESULT = res; :}
    	;

    class ::= classKwd_t identifier_t:id Indent_t objd:inner Dedent_t {: RESULT = new ClassDeclaration((String)id, "", "",
    	(inner instanceof DeclSequence)?(DeclSequence)inner : new DeclSequence((Declaration)inner), new FileLocation(currentState.pos)); :}
    	|	  classKwd_t identifier_t:id Newline_t {:RESULT = new ClassDeclaration((String)id, "", "", null, new FileLocation(currentState.pos)); :}
    	;

    otypeasc ::= typeasc:ta {: RESULT = ta; :}
    			| {: RESULT = null; :}
    			;
    non terminal declbody;
    declbody ::= equals_t dsle:r {: RESULT = r; :} | Indent_t p:r Dedent_t {: RESULT = r; :};


    params ::= openParen_t iparams:ip closeParen_t {: RESULT = ip; :}
    		|  openParen_t closeParen_t {: RESULT = new LinkedList<NameBinding>(); :}
    		;

   	iparams ::= identifier_t:id typeasc:ta comma_t iparams:re {: ((LinkedList<NameBinding>)re).addFirst(new NameBindingImpl((String)id, (Type)ta)); RESULT = re; :}
   			 |	identifier_t:id typeasc:ta {: LinkedList<NameBinding> llnb = new LinkedList<NameBinding>(); llnb.add(new NameBindingImpl((String)id, (Type)ta)); RESULT = llnb; :}
   			 ;

    val ::= valKwd_t identifier_t:id otypeasc:ty declbody:body {: RESULT = new ValDeclaration((String)id, (Type)ty, (TypedAST)body, new FileLocation(currentState.pos)); :};

    def ::= defKwd_t identifier_t:name params:argNames typeasc:fullType declbody:body {: RESULT = new DefDeclaration((String)name, (Type)fullType, (List<NameBinding>)argNames, (TypedAST)body, false, new FileLocation(currentState.pos));:}
    	;

    var ::= varKwd_t identifier_t:id typeasc:type declbody:body {: RESULT = new VarDeclaration((String)id, (Type)type, (TypedAST)body); :}
    	;

    objd ::= objcd:cds objd:rst {: RESULT = DeclSequence.simplify(new DeclSequence(Arrays.asList((TypedAST)cds, (TypedAST)rst))); :}
    	|	 objcld:ld  {: RESULT = ld; :}
    	|	 objrd:rest {: RESULT = rest; :}
    	;

    objrd ::= objid:rd objrd:rst {: RESULT = DeclSequence.simplify(new DeclSequence(Arrays.asList((TypedAST)rd, (TypedAST)rst))); :}
    	|	  obljid:rd {: RESULT = rd; :}
    	;

    objcd ::= classKwd_t defKwd_t identifier_t:name params:argNames typeasc:fullType declbody:body {: RESULT = new DefDeclaration((String)name, (Type)fullType, (List<NameBinding>)argNames, (TypedAST)body, true, new FileLocation(currentState.pos));:}
                            	;

    non terminal cbval;
    non terminal ctbval;

    non terminal cbvar;
    non terminal cbdef;
    non terminal cbdeclbody;

    non terminal cbvalbody;
    non terminal cbevalbody;

    objid ::= ctbval:va {: RESULT = va; :}
    	|	  var:va {: RESULT = va; :}
    	|	  def:va {: RESULT = va; :};

    obljid ::= cbval:va {: RESULT = va; :}
    	|	   cbvar:va {: RESULT = va; :}
    	|      cbdef:va {: RESULT = va; :}
    	;

    cbdeclbody ::= equals_t dslce:r {: RESULT = r; :} | Indent_t p:r Dedent_t {: RESULT = r; :};
    cbevalbody ::= cbdeclbody:bdy {: RESULT = bdy; :}| {: RESULT = null; :};
    cbvalbody ::= declbody:bdy {: RESULT = bdy; :}| Newline_t {: RESULT = null; :};

    objcld ::= classKwd_t defKwd_t identifier_t:name params:argNames typeasc:fullType cbdeclbody:body {: RESULT = new DefDeclaration((String)name, (Type)fullType, (List<NameBinding>)argNames, (TypedAST)body, true, new FileLocation(currentState.pos));:}
                            	;

    cbval ::= valKwd_t identifier_t:id otypeasc:ty cbevalbody:body {: RESULT = new ValDeclaration((String)id, (Type)ty, (TypedAST)body, new FileLocation(currentState.pos)); :};
	ctbval ::= valKwd_t identifier_t:id otypeasc:ty cbvalbody:body {: RESULT = new ValDeclaration((String)id, (Type)ty, (TypedAST)body, new FileLocation(currentState.pos)); :};

    cbdef ::= defKwd_t identifier_t:name params:argNames typeasc:fullType cbdeclbody:body {: RESULT = new DefDeclaration((String)name, (Type)fullType, (List<NameBinding>)argNames, (TypedAST)body, false, new FileLocation(currentState.pos));:}
    	;

    cbvar ::= varKwd_t identifier_t:id typeasc:type cbdeclbody:body {: RESULT = new VarDeclaration((String)id, (Type)type, (TypedAST)body); :};


    typedec ::= typeKwd_t identifier_t:name Indent_t typed:body Dedent_t {: RESULT = new TypeDeclaration((String)name, (DeclSequence)body, new FileLocation(currentState.pos)); :}
    	|	    typeKwd_t identifier_t:name Newline_t {: RESULT = new TypeDeclaration((String)name, null, null); :}
    	;

    typed ::= tdef:def Newline_t typed:rest {: RESULT = new DeclSequence(Arrays.asList((TypedAST)def, (TypedAST)rest)); :}
    	   |  tdef:def {: RESULT = new DeclSequence(Arrays.asList(new TypedAST[] {(TypedAST)def})); :}
    	   |  metadata:md {: RESULT = new DeclSequence(Arrays.asList(new TypedAST[] {(TypedAST)md})); :}
    	   ;

    tdef ::= defKwd_t identifier_t:name params:argNames typeasc:type {: RESULT = new DefDeclaration((String)name, (Type)type, (List<NameBinding>)argNames, null, false, new FileLocation(currentState.pos)); :};

    metadata ::= metadataKwd_t typeasc:type equals_t dslce:inner {: RESULT = new TypeDeclaration.AttributeDeclaration((TypedAST)inner, (Type)type); :};

    non terminal AE;
    non terminal ME;
    non terminal tle;

    dsle ::= tle:exn Newline_t {: RESULT = exn; :}
    | tle:exn dslSignal_t dslBlock:dsl {:
    	ASTExplorer exp = new ASTExplorer();
    	exp.transform((TypedAST) exn);
    	if (!exp.foundTilde())
			throw new RuntimeException();
		((DSLLit)exp.getRef()).setText((String)dsl);
    	RESULT = exn;
    :}
    | tle:exn newSignal_t newBlock:blk {:
		ASTExplorer exp = new ASTExplorer();
		exp.transform((TypedAST) exn);
		if (!exp.foundNew())
			throw new RuntimeException();
		((New)exp.getRef()).setBody((blk instanceof DeclSequence) ? (DeclSequence)blk : new DeclSequence(Arrays.asList((TypedAST)blk)));
		RESULT = exn;
	:};

    dslce ::= tle:exn {: RESULT = exn; :}
    | tle:exn dslSignal_t dslBlock:dsl {:
		ASTExplorer exp = new ASTExplorer();
		exp.transform((TypedAST) exn);
		if (!exp.foundTilde())
			throw new RuntimeException();
		((DSLLit)exp.getRef()).setText((String)dsl);
		RESULT = exn;
	 :}
    | tle:exn newSignal_t newCBlock:blk {:
		ASTExplorer exp = new ASTExplorer();
		exp.transform((TypedAST) exn);
		if (!exp.foundNew())
			throw new RuntimeException();
		((New)exp.getRef()).setBody((blk instanceof DeclSequence) ? (DeclSequence)blk : new DeclSequence(Arrays.asList((TypedAST)blk)));
		RESULT = exn;
	 :};

	tle ::= e:aer {:
		ASTExplorer exp = new ASTExplorer();
		exp.transform((TypedAST) aer);
		if (exp.foundNew()){
			pushToken(Terminals.newSignal_t,"");
		}else if (exp.foundTilde()) {
			pushToken(Terminals.dslSignal_t,"");
		}

		RESULT = aer;
	:};

    e ::= AE:aer {:
    	RESULT = aer;
    :};

    AE ::= AE:l plus_t ME:r {: RESULT = new Invocation((TypedAST)l,"+",(TypedAST)r, new FileLocation(currentState.pos)); :}
    	|  AE:l dash_t ME:r {: RESULT = new Invocation((TypedAST)l,"-",(TypedAST)r, new FileLocation(currentState.pos)); :}
    	|  ME:mer {:RESULT = mer;:};

    ME ::= ME:l mult_t term:r {: RESULT = new Invocation((TypedAST)l,"*",(TypedAST)r, new FileLocation(currentState.pos)); :}
    	|  ME:l divide_t term:r {: RESULT = new Invocation((TypedAST)l,"/",(TypedAST)r, new FileLocation(currentState.pos)); :}
    	|  term:ter {:RESULT = ter;:};

    non terminal etuple;

    term ::= identifier_t:id {: RESULT = new Variable(new NameBindingImpl((String)id, null), new FileLocation(currentState.pos)); :}
    	|	 fnKwd_t identifier_t:id typeasc:t arrow_t openParen_t e:inner closeParen_t {: RESULT = new Fn(Arrays.asList(new NameBindingImpl((String)id, (Type)t)), (TypedAST)inner); :}
    	|	 openParen_t e:inner closeParen_t {: RESULT = inner; :}
    	|	 etuple:tpe {: RESULT = tpe; :}
    	|	 term:src tuple:tgt {: RESULT = new Application((TypedAST)src, (TypedAST)tgt, new FileLocation(currentState.pos)); :}
    	|	 term:src dot_t identifier_t:op {: RESULT = new Invocation((TypedAST)src,(String)op, null, new FileLocation(currentState.pos)); :}
    	//|	 term:src typeasc:as {: RESULT = new TypeAsc((Expr)src, (Type)as); :}
    	|	 inlinelit:lit {: RESULT = new DSLLit(Optional.of((String)lit)); :}
    	|	 decimalInteger_t:res {: RESULT = new IntegerConstant((Integer)res); :}
    	|	 newKwd_t {: RESULT = new New(new HashMap<String,TypedAST>(), new FileLocation(currentState.pos)); :}
    	|	 tilde_t {: RESULT = new DSLLit(Optional.empty()); :}
    	|	 shortString_t:str {: RESULT = new StringConstant((String)str); :}
    	;

    etuple ::= openParen_t e:first comma_t it:rest closeParen_t {: RESULT = new TupleObject((TypedAST)first,(TypedAST)rest, new FileLocation(currentState.pos)); :}
                   		| openParen_t closeParen_t {: RESULT = UnitVal.getInstance(null); :}
                   		;

    tuple ::= openParen_t it:res closeParen_t {:RESULT = res; :}
    		| openParen_t closeParen_t {: RESULT = UnitVal.getInstance(null); :}
    		;

   	it ::= e:first comma_t it:rest {: RESULT = new TupleObject((TypedAST)first,(TypedAST)rest, new FileLocation(currentState.pos)); :}
   		|  e:el {: RESULT = el; :}
   		;


   	type ::= type:t1 tarrow_t type:t2 {: RESULT = new Arrow((Type)t1,(Type)t2); :}
   		|	 type:t1 mult_t type:t2 {: RESULT = new Tuple((Type)t1,(Type)t2); :}
   		|	 openParen_t type:ta closeParen_t {: RESULT = ta; :}
   		|	 identifier_t:id {: RESULT = new UnresolvedType((String)id); :}
   		;

   	typeasc ::= colon_t type:ty {: RESULT = ty; :};

   	non terminal innerdsl;
    precedence left oCurly_t;

   	inlinelit ::= oCurly_t innerdsl:idsl cCurly_t {: RESULT = idsl; :};

   	innerdsl ::= notCurly_t:str {: RESULT = str; :} | notCurly_t:str oCurly_t innerdsl:idsl cCurly_t innerdsl:stre {: RESULT = str + "{" + idsl + "}" + stre; :} | {: RESULT = ""; :};

	non terminal dslLine;
	non terminal dslStart;

   	dslBlock ::= Indent_t dslStart:dsl Dedent_t {: RESULT = dsl; :};
   	dslStart ::= dslLine_t:s {: RESULT = s; :} | dslLine_t:st dslInner:i {: RESULT = (String)st + (String)i; :};
   	dslInner ::= dslLine:i {: RESULT = i; :}| dslLine:i dslInner:n {: RESULT = (String)i + (String)n; :};
   	dslLine ::= dslWhitespace_t:ws dslLine_t:ln {: RESULT = (String)ws + (String)ln; :};

   	newBlock ::= Indent_t objrd:inner Dedent_t {: RESULT = inner; :} | Newline_t {: RESULT = new DeclSequence(); :};
   	newCBlock ::= Indent_t objrd:inner Dedent_t {: RESULT = inner; :} | {: RESULT = new DeclSequence(); :};

%cf}