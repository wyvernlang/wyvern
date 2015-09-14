This file describes the package structure of the Wyvern tool suite.
View this document with tabs set to 4 characters.

To compile Wyvern, use Java 8 and JUnit 4, and ant 1.9.0 (or higher).
Eclipse version 4.3 with Java 8 support works, earlier versions will not.
To compile, just run `ant build`.

To run Wyvern from the command line, make sure Java 8 is on your path.
Add `wyvern\tools\build` to your CLASSPATH, and run "java wyvern.tools.util.CLI filename.wyv"


PACKAGE DESCRIPTIONS

wyvern.tools.errors					errors sent during parsing, evaluation, or
									typechecking
wyvern.tools.lexer					the lexer
wyvern.tools.parsing				core phase 2 parsing classes and interfaces
wyvern.tools.parsing.extensions		parsing for concrete constructs
wyvern.tools.rawAST					the raw hierarchical AST
wyvern.tools.simpleParser			phase 1 parser
wyvern.tools.tests					test cases
wyvern.tools.tests.samples			test data
wyvern.tools.typedAST				the typed AST
wyvern.tools.typedAST.binding		classes and interfaces for binding
wyvern.tools.typedAST.extensions	ASTs for concrete constructs
wyvern.tools.types					core types and interfaces for them
wyvern.tools.types.extensions		types for concrete constructs
wyvern.tools.util					utilities for printing ASTs and pairs
