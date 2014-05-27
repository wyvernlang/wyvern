package wyvern.tools.parsing.parselang;
import wyvern.tools.types.*;
import wyvern.tools.types.extensions.*;

%%
%parser TypeParser

%lex{
	terminal identifier_t ::= /[a-zA-Z_][a-zA-Z_0-9]*/ in (), < (), > () {:
               		RESULT = lexeme;
               	:};

 	terminal tarrow_t ::= /-\>/ ;
	terminal mult_t ::= /\*/ ;
	terminal dot_t ::= /\./ ;
	terminal openParen_t ::= /\(/ ;
	terminal closeParen_t ::= /\)/ ;
%lex}

%cf{
	non terminal type;

   	precedence right tarrow_t;
    precedence left dot_t;
    precedence left mult_t;

	start with type;

   	type ::= type:t1 tarrow_t type:t2 {: RESULT = new Arrow((Type)t1,(Type)t2); :}
   		|	 type:t1 mult_t type:t2 {: RESULT = new Tuple((Type)t1,(Type)t2); :}
   		|    type:t dot_t identifier_t:el {: RESULT = new TypeInv((Type)t, (String)el); :}
   		|	 openParen_t type:ta closeParen_t {: RESULT = ta; :}
   		|	 identifier_t:id {: RESULT = new UnresolvedType((String)id); :}
   		;
%cf}