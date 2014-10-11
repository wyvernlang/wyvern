Differences between TSL Wyvern and the Wyvern implementation.
==========================

The Wyvern implementation is intended to support a large number of systems beyond those described in our ECOOP 2014 paper, and as a result, a number of interfaces work differently. We will use the term TSL Wyvern for the version described in the paper, and Wyvern for the language as implemented.

There are three notable components to this difference: the types involved in parsing, the parsing TSL, and the intermediate AST representation. We will discuss each in turn.

Types involved in parsing
---------------
The names associated with extension parsers are the same. However, one change has been made for the purposes of implementation ease.

In TSL Wyvern, the type HasParser has signature

    type HasParser
        val parser:ExtParser

Wyvern implements this as

    type HasParser
        def getParser():ExtParser

ExtParser has been modified slightly as well, with the argument type for parse being called ParseBuffer instead of ParseStream.

Parsing TSL
----------------
The parsing TSL as-implemented differs substantially from the one that we describe for TSL Wyvern. As no reliable implementation of Adams' formalism exists, we instead use the Copper parser generator from Schwerdfeger et al. The parser TSL as-exposed sits on top of this parser generator, and replaces Java inside of the Copper parser description. For a description of the Copper parser generator, we defer to [the official documentation](http://melt.cs.umn.edu/copper/).

AST representation
-------------------
Wyvern does not implement the typed elaboration mechanism used by TSL Wyvern by having two separate AST representations for typed and untyped abstract syntax elements. Instead, it uses one shared representation that is updated mutably at typecheck type. TSL references are resolved in a separate terminal replacement pass. This sole representation is called TypedAST by Wyvern, and is the return type for all parsers.


Example
---------------
We will examine a simple TSL for the purposes of illustration.

    import java:wyvern.tools.typedAST.interfaces.TypedAST
	import java:wyvern.tools.parsing.HasParser
	import java:wyvern.tools.parsing.ExtParser
	import java:wyvern.tools.parsing.ParseBuffer
	
We import the relevant compiler components from Java. Each will be bound as one expects.
	
	type Hello
	
The type to add a TSL to

		def get():Int

The method to fetch the TSL value

		metadata:HasParser = new
			def getParser():ExtParser
				new
					def parse(buf:ParseBuffer):TypedAST

This shows the signature for parse. Note the different name from TSL Wyvern.

		    			val iparser:ExtParser = ~
		    			
Enter the ExtParser TSL context. This is implemented in Copper from this point onwards.
		    			
							%%
							%parser HelloWorld
							%lex{
								terminal TypedAST hello_t ::= /hello/ {:
									~
										2
								:};
								terminal Int world_t ::= /world/ {: 1 :};
								terminal Unit space_t ::= / / {: () :};
							%lex}
							%cf{
								non terminal TypedAST helloworld;
								start with helloworld;
								helloworld ::= hello_t:a space_t world_t:b {: a :};
							%cf}

Done with the declarative specification, we now apply the parser to the input.

						val parsed = iparser.parse(buf)
						
We now use the splicing mechansiim to embed the output AST into a object creation statement.
						~
							new
								def get():Int = $parsed
								
This example parses the string "hello world", and results in an object that returns 2.
