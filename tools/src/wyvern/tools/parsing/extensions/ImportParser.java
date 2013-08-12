package wyvern.tools.parsing.extensions;

import wyvern.DSL.DSL;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.ToolError;
import wyvern.tools.parsing.ContParser;
import wyvern.tools.parsing.DeclParser;
import wyvern.tools.parsing.LineParser;
import wyvern.tools.parsing.ParseUtils;
import wyvern.tools.rawAST.StringLiteral;
import wyvern.tools.typedAST.core.declarations.ImportDeclaration;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.types.Environment;
import wyvern.tools.util.CompilationContext;
import wyvern.tools.util.Pair;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;

/**
 * Created by Ben Chung on 8/9/13.
 */
public class ImportParser implements DeclParser {

	@Override
	public TypedAST parse(TypedAST first, CompilationContext ctx) {
		throw new RuntimeException();
	}

	@Override
	public Pair<Environment, ContParser> parseDeferred(TypedAST first, CompilationContext ctx) {
		StringLiteral importLiteral = (StringLiteral)ctx.popToken();
		String importName = importLiteral.data;
		ParseUtils.parseSymbol("as", ctx);
		String alias = ParseUtils.parseSymbol(ctx).name;
		Pair<Environment, ContParser> parserPair = null;
		try {
			parserPair = wyvern.stdlib.Compiler.compilePartial(URI.create(importName), new ArrayList<DSL>());
		} catch (IOException e) {
			ToolError.reportError(ErrorMessage.UNEXPECTED_INPUT, importLiteral);
		}
		final ImportDeclaration declaration = new ImportDeclaration(importName, alias, parserPair.first, importLiteral.getLocation());
		final Pair<Environment, ContParser> finalParserPair = parserPair;
		return new Pair<Environment, ContParser>(declaration.extend(Environment.getEmptyEnvironment()), new ContParser() {
			@Override
			public TypedAST parse(EnvironmentResolver r) {
				TypedAST result = finalParserPair.second.parse(r);
				declaration.setAST(result);
				return declaration;
			}
		});
	}
}
