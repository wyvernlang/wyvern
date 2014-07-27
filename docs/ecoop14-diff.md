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
