Wyvern Compiler Structure
=========================

As the majority of the compiler resides within wyvern.tools, we will begin our discussion by focusing on this section.

* errors

  This package contains the standard error handling mechanism provided by the language. The most notable classes are ErrorMessage and ToolError. The first defines several standard strings for given errors, and exists primarily to facilitate localization. The latter provides a standardized mechanism for throwing errors that requires and ErrorMessage and a file location.

* interpreter

  This is currently a (mostly) functional frontend to the Wyvern compiler+interpreter.

* lexer

  This package contains the tokenizer and lexer, combined into the same state machine. Lexer is the entry point, converting a Reader from a file described by filename into a RawAST with appropriate elements.

* parsing

  This is the core of the Wyvern compiler, containing the top-level parsers (BodyParser and DeclarationParser) as well as the extension parsers. The top-level parsers are called on the RawAST and parse it into a TypedAST, using an environment that is passed in to them as context. In addition, this environment may contain several extension parsers.

* rawAST

  RawAST contains several classes designed to present a low-level view of the file to parsers. Parsers are required to take a RawAST element of some sort and transform it into a TypedAST or a late-bound TypedAST representation. As such, RawAST is a simple line-oriented view of the input that is very close to the file itself.

* simpleParser

  simpleParser contains a single class, Phase1Parser. This acts as a frontend for the Lexer as well as an additional level of tokenization. This class primarily exists to handle nesting either via parens or indentation.

* tests

  Somewhat self-explanatory.

* typedAST

  TypedAST is the "core" of the language, providing the highest-level picture of the implementation. This contains enough material that we break it into the following sections

    * interfaces

      This contains the skeleton of the AST.

    * abs

      Analogous to interfaces, but for abstract classes.

    * core

      AST elements that are linked to a theory.

    * extensions

      AST elements that are not linked to theory, and must therefore be translatable to elements within core.

    * builtin

      Components that native to the implementation, e.g. String, Integer.

* types

  The typesystem of the language

* util

  The various utilities used by the language.