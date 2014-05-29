package wyvern.tools.parsing.quotelang;
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
import wyvern.tools.parsing.DSLLit;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.HashMap;
import java.util.*;
import wyvern.tools.errors.FileLocation;
import java.net.URI;

%%
%parser WyvernQuote

%aux{
	Integer parenLevel, cl;
	Stack<Integer> depths;
	Pattern nlRegex;
	boolean nextDsl, nextDedent;
%aux}

%init{
	parenLevel = 0;
	cl = 0;
	nextDsl = false;
	nextDedent = false;
	depths = new Stack<Integer>();
	depths.push(0);
	nlRegex = Pattern.compile("(\r\n|\n)([\t ]*)");
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
			output = inputPattern.group(2);
		}
		int newDepth = output.length();
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
			output = inputPattern.group(2);
		}
		int newDepth = output.length();
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
			output = inputPattern.group(2);
		}
		int newDepth = output.length();
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
			output = inputPattern.group(2);
		}
		int newDepth = output.length();
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
			output = inputPattern.group(2);
		}
		int newDepth = output.length();
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
			output = inputPattern.group(2);
		}
		int newDepth = output.length();
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
		throw new RuntimeException(new FileLocation(currentState.pos) + " state:" + currentState.statenum);
	:};



	class keywds;
    class specialNumbers;

	terminal Indent_t ::= /(((\r\n)|\n)([ \t]*))+/
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

	terminal Dedent_t ::= /(((\r\n)|\n)([ \t]*))+/
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
			pushToken(Terminals.Dedent_t,lexeme);
		} else {
			pushToken(Terminals.Newline_t,lexeme);
		}
	:};

    terminal Newline_t ::= /((\n|(\r\n))([ \t]*))+/ {: :};

	terminal DedentRepair_t ::= /(\n[ \t]*)+/
	{:
		pushToken(Terminals.Dedent_t,lexeme);
	:};



	ignore terminal comment_t  ::= /\/\/([^\r\n])*/;
	ignore terminal multi_comment_t  ::= /\/\*(.|\n|\r)*?\*\//;
	ignore terminal ignoredNewline ::= /((\n|(\r\n))[ \t]*)+/;
	ignore terminal Spaces_t ::= /[ \t]+|(\\(\n|(\r\n)))/;

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
 	terminal importKwd_t   ::= /import/ in (keywds);
 	terminal moduleKwd_t   ::= /module/ in (keywds);
	terminal ifKwd_t 	::= /if/ in (keywds);
 	terminal thenKwd_t   ::= /then/ in (keywds);
 	terminal elseKwd_t   ::= /else/ in (keywds);

 	terminal decimalInteger_t ::= /([1-9][0-9]*)|0/ {:
 		RESULT = Integer.parseInt(lexeme);
 	:};

 	terminal ialpha_t ::= /[a-zA-Z][a-zA-Z0-9\$\-\_\@\.\&\!\*\"\'\(\)\,\%]*/ {: RESULT = lexeme; :};
 	terminal xalphas_t ::= /[a-zA-Z0-9\$\-\_\@\.\&\!\*\"\'\(\)\,\%]+/ {: RESULT = lexeme; :};
 	terminal xpalphas_t ::= /[a-zA-Z0-9\+\$\-\_\@\.\&\!\*\"\'\(\)\,\%]+/ {: RESULT = lexeme; :};

	disambiguate xpa1:(xpalphas_t,identifier_t){: return xpalphas_t; :};
	disambiguate xpa2:(xpalphas_t,decimalInteger_t){: return xpalphas_t; :};
	disambiguate xpa3:(xpalphas_t,openParen_t){: return xpalphas_t; :};
	disambiguate xpa4:(xpalphas_t,shortString_t){: return xpalphas_t; :};
	disambiguate xpa5:(xpalphas_t,classKwd_t){: return xpalphas_t; :};
	disambiguate xpa6:(xpalphas_t,typeKwd_t){: return xpalphas_t; :};
	disambiguate xpa7:(xpalphas_t,valKwd_t){: return xpalphas_t; :};
	disambiguate xpa8:(xpalphas_t,defKwd_t){: return xpalphas_t; :};
	disambiguate xpa9:(xpalphas_t,varKwd_t){: return xpalphas_t; :};
	disambiguate xpa10:(xpalphas_t,fnKwd_t){: return xpalphas_t; :};
	disambiguate xpa11:(xpalphas_t,metadataKwd_t){: return xpalphas_t; :};
	disambiguate xpa12:(xpalphas_t,newKwd_t){: return xpalphas_t; :};
	disambiguate xpa13:(xpalphas_t,importKwd_t){: return xpalphas_t; :};

	terminal tilde_t ::= /~/ ;
	terminal plus_t ::= /\+/ ;
	terminal dash_t ::= /-/ ;
	terminal mult_t ::= /\*/ ;
	terminal divide_t ::= /\// ;
	terminal equals_t ::= /=/ ;
	terminal equalsequals_t ::= /==/ ;
	terminal openParen_t ::= /\(/  {:  parenLevel++; :};
 	terminal closeParen_t ::= /\)/ {:  parenLevel--; :};
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
 	terminal dollar_t ::= /$/ ;


 	terminal shortString_t ::= /(('([^'\n]|\\.|\\O[0-7])*')|("([^"\n]|\\.|\\O[0-7])*"))|(('([^']|\\.)*')|("([^"]|\\.)*"))/ {:
 		RESULT = lexeme.substring(1,lexeme.length()-1);
 	:};

 	terminal oCurly_t ::= /\{/ {: cl++; :};
 	terminal cCurly_t ::= /\}/ {: cl--; :};
 	terminal notCurly_t ::= /[^\{\}]*/ {: RESULT = lexeme; :};

 	terminal dslWhitespace_t ::= /(\n[ \t]*)+/ {: nextDsl = true; RESULT = "\n"+lexeme.substring(depths.peek()+1); :};
 	terminal dslLine_t ::= /[^\n]+/ {: nextDsl = false; RESULT = lexeme; :};

 	terminal newSignal_t ::= /dgdkfghfugiehri/; //placeholders to make error messages sane
 	terminal dslSignal_t ::= /sdfgjtyertdvcx/;
 	terminal caseSignal_t ::= //;

 	terminal moduleName_t ::= /[a-zA-Z\.]+/ {: RESULT = lexeme; :};
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
   	non terminal dslSeq;
   	non terminal import;
   	non terminal fragaddr;
   	non terminal elst;
   	non terminal module;
   	non terminal pm;
   	non terminal dm;
   	non terminal mnrd;
   	non terminal ptl;

   	precedence left Dedent_t, Newline_t;
   	precedence right tarrow_t;
    precedence left colon_t;
    precedence left openParen_t;
    precedence left dot_t;
    precedence left equalsequals_t, lt_t, gt_t;
    precedence left equals_t;
    precedence left plus_t, dash_t;
    precedence left mult_t, divide_t;

    non terminal fc2;

	start with fc;

	fc2 ::= identifier_t;

	fc ::= import:imp Newline_t fc:nxt {: RESULT = new Sequence((TypedAST)imp, (TypedAST)nxt); :}
		 | module:mod ptl:prog {: RESULT = new ModuleDeclaration((String)mod, (EnvironmentExtender)prog, new FileLocation(currentState.pos));:}
		 | p:prog {: RESULT = prog; :};

	ptl ::= import:imp Newline_t ptl:nxt {: RESULT = DeclSequence.simplify(new DeclSequence((TypedAST)imp, (TypedAST)nxt)); :}
		|   import:imp {: RESULT = imp; :}
		|   pm:bdy {: RESULT = bdy; :};

	import ::= importKwd_t fragaddr:ur {: RESULT = new ImportDeclaration((URI)ur, new FileLocation(currentState.pos)); :};

	module ::= moduleKwd_t moduleName_t:id {: RESULT = id; :};

	pm ::= dm:ds {: RESULT = ds; :} | {: RESULT = new DeclSequence(); :};

	p ::= elst:ex {: RESULT = ex; :}
    	| d:de {: RESULT = de; :}
    	;

	non terminal pnrd;

    dm ::= prd:res Newline_t mnrd:after {: RESULT = new DeclSequence(Arrays.asList((TypedAST)res,(TypedAST)after)); :}
    	 | prd:res Newline_t {: RESULT = res; :}
    	 | prd:res  {: RESULT = res; :}
    	 | mnrd:res {: RESULT = res; :}
    	;

    mnrd ::= val:vd Newline_t pm:re {: RESULT = new DeclSequence(Arrays.asList((TypedAST)vd,(TypedAST)re)); :}
    	 |   var:vd Newline_t pm:re {: RESULT = new DeclSequence(Arrays.asList((TypedAST)vd,(TypedAST)re)); :}
    	 ;

    d ::= prd:res Newline_t pnrd:after {:RESULT = new Sequence(Arrays.asList((TypedAST)res,(TypedAST)after)); :}
    	| prd:res {: RESULT = res; :}
    	| nrd:res {: RESULT = res; :}
    	;

    non terminal lvalue;
    lvalue ::= identifier_t:id {: RESULT = new Variable(new NameBindingImpl((String)id, null), new FileLocation(currentState.pos)); :}
    	|	   term:prev dot_t identifier_t:id {: RESULT = new Invocation((TypedAST)prev, (String)id, null, new FileLocation(currentState.pos)); :};

    nrd ::= val:vd Newline_t p:re {: RESULT = new Sequence(Arrays.asList((TypedAST)vd,(TypedAST)re)); :}
    	|   var:vd Newline_t p:re {: RESULT = new Sequence(Arrays.asList((TypedAST)vd,(TypedAST)re)); :}
    	;

    prd ::= prd:re Newline_t rd:vd{: RESULT = DeclSequence.simplify(new DeclSequence(Arrays.asList((TypedAST)vd,(TypedAST)re))); :}
    	  | rd:vd {: RESULT = vd; :}
    	  ;

    pnrd ::= elst:ex {: RESULT = ex; :}| nrd:nr {: RESULT = nr; :};

    rd ::= class:res {: RESULT = res; :}
    	|  typedec:res {: RESULT = res; :}
    	|  def:res {: RESULT = res; :}
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

    class ::= classKwd_t identifier_t:id Indent_t objd:inner Dedent_t {: RESULT = new ClassDeclaration((String)id, "", "",
    	(inner instanceof DeclSequence)?(DeclSequence)inner : new DeclSequence((Declaration)inner), new FileLocation(currentState.pos)); :}
    	|	  classKwd_t identifier_t:id {:RESULT = new ClassDeclaration((String)id, "", "", null, new FileLocation(currentState.pos)); :}
    	;

    objd ::= objcd:cds Newline_t objd:rst {: RESULT = DeclSequence.simplify(new DeclSequence(Arrays.asList((TypedAST)cds, (TypedAST)rst))); :}
    	|	 objrd:rest {: RESULT = rest; :}
    	|	 objcd:cds {: RESULT = cds; :}
    	;

    objrd ::= objid:rd Newline_t objrd:rst {: RESULT = DeclSequence.simplify(new DeclSequence(Arrays.asList((TypedAST)rd, (TypedAST)rst))); :}
    	|	  objid:rd {: RESULT = rd; :}
    	;

    objcd ::= classKwd_t defKwd_t identifier_t:name params:argNames typeasc:fullType declbody:body {: RESULT = new DefDeclaration((String)name, (Type)fullType, (List<NameBinding>)argNames, (TypedAST)body, true, new FileLocation(currentState.pos));:}
                            	;

    non terminal cbval;


    non terminal cbvalbody;

    objid ::= cbval:va {: RESULT = va; :}
    	|	  var:va {: RESULT = va; :}
    	|	  def:va {: RESULT = va; :};

    cbvalbody ::= declbody:bdy {: RESULT = bdy; :}| {: RESULT = null; :};

    cbval ::= valKwd_t identifier_t:id otypeasc:ty cbvalbody:body {: RESULT = new ValDeclaration((String)id, (Type)ty, (TypedAST)body, new FileLocation(currentState.pos)); :};

    objcd ::= classKwd_t defKwd_t identifier_t:name params:argNames typeasc:fullType declbody:body {: RESULT = new DefDeclaration((String)name, (Type)fullType, (List<NameBinding>)argNames, (TypedAST)body, true, new FileLocation(currentState.pos));:}
                            	;

    typedec ::= typeKwd_t identifier_t:name Indent_t typed:body Dedent_t {: RESULT = new TypeDeclaration((String)name, (DeclSequence)body, new FileLocation(currentState.pos)); :}
    	|	    typeKwd_t identifier_t:name {: RESULT = new TypeDeclaration((String)name, null, new FileLocation(currentState.pos)); :}
    	;

    typed ::= tdef:def Newline_t typed:rest {: RESULT = new DeclSequence(Arrays.asList((TypedAST)def, (TypedAST)rest)); :}
    	   |  tdef:def {: RESULT = new DeclSequence(Arrays.asList(new TypedAST[] {(TypedAST)def})); :}
    	   |  metadata:md {: RESULT = new DeclSequence(Arrays.asList(new TypedAST[] {(TypedAST)md})); :}
    	   ;

    tdef ::= defKwd_t identifier_t:name params:argNames typeasc:type {: RESULT = new DefDeclaration((String)name, (Type)type, (List<NameBinding>)argNames, null, false, new FileLocation(currentState.pos)); :};

    metadata ::= metadataKwd_t typeasc:type equals_t dsle:inner {: RESULT = new TypeDeclaration.AttributeDeclaration((TypedAST)inner, (Type)type); :};

    non terminal AE;
    non terminal ME;
    non terminal tle;

    elst ::= dsle:ce {: RESULT = ce; :}
    	|    dsle:ce Newline_t p:lst {: RESULT = new Sequence((TypedAST)ce,(TypedAST)lst); :}
    	| ;

    dsle ::= tle:exn {: RESULT = exn; :}
    | tle:exn dslSignal_t dslSeq:dsl {:
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

	non terminal colonApp;

	tle ::= colonApp:aer {:
		ASTExplorer exp = new ASTExplorer();
		exp.transform((TypedAST) aer);
		if (exp.foundNew()){
			pushToken(Terminals.newSignal_t,"");
		}else if (exp.foundTilde()) {
			pushToken(Terminals.dslSignal_t,"");
		}

		RESULT = aer;
	:};

	colonApp ::= e:src {: RESULT = src; :} | term:src tuple:tgt colon_t {: RESULT = new Application((TypedAST)src,
		new TupleObject((TypedAST)tgt, new DSLLit(Optional.empty()), new FileLocation(currentState.pos)),
		new FileLocation(currentState.pos)); :};

	non terminal a;

    e ::= fnKwd_t identifier_t:id typeasc:t arrow_t e:inner {: RESULT = new Fn(Arrays.asList(new NameBindingImpl((String)id, (Type)t)), (TypedAST)inner); :}
    	|	 openParen_t e:src typeasc:as closeParen_t {: RESULT = new TypeAsc((TypedAST)src, (Type)as); :}
    	|	 ifKwd_t e:check thenKwd_t e:thenE elseKwd_t e:elseE {:
    		RESULT = new IfExpr(Arrays.asList(new IfExpr.CondClause((TypedAST)check, (TypedAST)thenE, new FileLocation(currentState.pos)),
    										  new IfExpr.UncondClause((TypedAST)elseE, new FileLocation(currentState.pos))), new FileLocation(currentState.pos)); :}
    	| a:aer {: RESULT = aer; :};

	non terminal EE;
	non terminal BE;
	non terminal ASSN;

    a ::= ASSN:are {: RESULT = are; :};

	ASSN ::= lvalue:to equals_t e:va {:RESULT = new Assignment((TypedAST)to, (TypedAST)va, new FileLocation(currentState.pos));:}
		 |   EE:va {: RESULT = va; :};

    EE ::= EE:l equalsequals_t EE:r {: RESULT = new Invocation((TypedAST)l,"==",(TypedAST)r, new FileLocation(currentState.pos)); :}
    	|  EE:l gt_t EE:r {: RESULT = new Invocation((TypedAST)l,">",(TypedAST)r, new FileLocation(currentState.pos)); :}
    	|  EE:l lt_t EE:r {: RESULT = new Invocation((TypedAST)l,"<",(TypedAST)r, new FileLocation(currentState.pos)); :}
    	|  AE:mer {:RESULT = mer;:};

    AE ::= AE:l plus_t ME:r {: RESULT = new Invocation((TypedAST)l,"+",(TypedAST)r, new FileLocation(currentState.pos)); :}
    	|  AE:l dash_t ME:r {: RESULT = new Invocation((TypedAST)l,"-",(TypedAST)r, new FileLocation(currentState.pos)); :}
    	|  ME:mer {:RESULT = mer;:};

    ME ::= ME:l mult_t BE:r {: RESULT = new Invocation((TypedAST)l,"*",(TypedAST)r, new FileLocation(currentState.pos)); :}
    	|  ME:l divide_t BE:r {: RESULT = new Invocation((TypedAST)l,"/",(TypedAST)r, new FileLocation(currentState.pos)); :}
    	|  BE:ter {:RESULT = ter;:};

    BE ::= BE:l bar_t bar_t term:r {: RESULT = new Invocation((TypedAST)l, "||", (TypedAST)r, new FileLocation(currentState.pos)); :}
    	|  BE:l and_t and_t term:r {: RESULT = new Invocation((TypedAST)l, "&&", (TypedAST)r, new FileLocation(currentState.pos)); :}
    	|  term:ter {:RESULT = ter;:};


    non terminal etuple;


    term ::= lvalue:lv {: RESULT = lv; :}
    	|	 openParen_t e:inner closeParen_t {: RESULT = inner; :}
    	|	 etuple:tpe {: RESULT = tpe; :}
    	|	 term:src tuple:tgt {: RESULT = new Application((TypedAST)src, (TypedAST)tgt, new FileLocation(currentState.pos)); :}
    	|	 inlinelit:lit {: RESULT = new DSLLit(Optional.of((String)lit)); :}
    	|	 decimalInteger_t:res {: RESULT = new IntegerConstant((Integer)res); :}
    	|	 newKwd_t {: RESULT = new New(new HashMap<String,TypedAST>(), new FileLocation(currentState.pos)); :}
    	|	 tilde_t {: RESULT = new DSLLit(Optional.empty()); :}
    	|	 shortString_t:str {: RESULT = new StringConstant((String)str); :}
    	|	 dollar_t identifier_t:id {: RESULT = new ToastExpression(new SpliceExn(new Variable(new NameBindingImpl((String)id, null), new FileLocation(currentState.pos))));:}
    	|	 dollar_t openParen_t term:inner closeParen_t {: RESULT = new ToastExpression(new SpliceExn((TypedAST)inner)); :}
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
   		|    type:t dot_t identifier_t:el {: RESULT = new TypeInv((Type)t, (String)el); :}
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

	dslSeq ::= dslBlock:blk  {: RESULT = blk; :}
			 | dslBlock:blk1 dslSeq:blk2 {: RESULT = (String)blk1 + "\n" + (String)blk2;:};
   	dslBlock ::= Indent_t dslStart:dsl Dedent_t {: RESULT = dsl; :};
   	dslStart ::= dslLine_t:s {: RESULT = s; :} | dslLine_t:st dslInner:i {: RESULT = (String)st + (String)i; :};
   	dslInner ::= dslLine:i {: RESULT = i; :}| dslLine:i dslInner:n {: RESULT = (String)i + (String)n; :};
   	dslLine ::= dslWhitespace_t:ws dslLine_t:ln {: RESULT = (String)ws + (String)ln; :};

   	newBlock ::= Indent_t objrd:inner Dedent_t {: RESULT = inner; :} | {: RESULT = new DeclSequence(); :};

   	//URIs
   	non terminal uri;
   	non terminal path;
   	non terminal scheme;
   	non terminal fragmentid;
   	non terminal search;

   	fragaddr ::= uri:re {:RESULT=re;:} | uri:re pound_t fragmentid:fid {:
   		try {
   		RESULT =  new URI(((URI)re).getScheme(), ((URI)re).getSchemeSpecificPart(), (String)fid);
   		} catch (Exception e) { throw new RuntimeException(e); }
   	:};
   	uri ::= scheme:schm colon_t path:pth {:
			try {
			RESULT = new URI((String)schm,(String)pth,"");
			} catch (Exception e) { throw new RuntimeException(e); }
   		:};
   	path ::= xpalphas_t:xa {: RESULT = xa; :} | xpalphas_t:xa divide_t path:xb {:RESULT = (String)xa + "/" + (String)xb; :} | {:RESULT = "";:};
   	scheme ::= ialpha_t:sch {:RESULT=sch;:};
   	fragmentid ::= xalphas_t:xa{:RESULT=xa;:};
   	search ::= xalphas_t:xa{:RESULT=xa;:} | xalphas_t:xa plus_t search:sh {:RESULT=(String)xa + (String)sh;:};


%cf}