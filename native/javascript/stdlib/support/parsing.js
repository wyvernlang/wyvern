const nearley = require("nearley");
const compile = require("nearley/lib/compile");
const generate = require("nearley/lib/generate");
const nearleyGrammar = require("nearley/lib/nearley-language-bootstrapped");

exports.makeParser = function (sourceCode, theLexer) {
    return exports.makeParserWithContext(sourceCode, theLexer, undefined);
}

exports.makeParserWithContext = function (sourceCode, theLexer, context) {
    // Parse the grammar source into an AST
    const grammarParser = new nearley.Parser(nearleyGrammar);
    sourceCode = "@lexer theLexer\n" + sourceCode;
    grammarParser.feed(sourceCode);
    const grammarAst = grammarParser.results[0]; // TODO check for errors

    // Compile the AST into a set of rules
    const grammarInfoObject = compile(grammarAst, {});
    // Generate JavaScript code from the rules
    const grammarJs = generate(grammarInfoObject, "grammar");

    // Pretend this is a CommonJS environment to catch exports from the grammar.
    const module = { exports: {} };
    // context is in scope for this evaluation
    eval(grammarJs);

    return new nearley.Parser(nearley.Grammar.fromCompiled(module.exports));
}
