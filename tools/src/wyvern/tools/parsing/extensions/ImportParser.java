package wyvern.tools.parsing.extensions;

import wyvern.tools.parsing.ContParser;
import wyvern.tools.parsing.DeclParser;
import wyvern.tools.parsing.LineParser;
import wyvern.tools.parsing.ParseUtils;
import wyvern.tools.typedAST.core.declarations.ImportDeclaration;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.types.Environment;
import wyvern.tools.util.CompilationContext;
import wyvern.tools.util.Pair;

/**
 * Created by Ben Chung on 8/9/13.
 */
public class ImportParser implements DeclParser {

	@Override
	public TypedAST parse(TypedAST first, CompilationContext ctx) {
		String importName = ParseUtils.parseStringLiteral(ctx);
		ParseUtils.parseSymbol("as", ctx);
		String alias = ParseUtils.parseSymbol(ctx).name;
		return new ImportDeclaration(importName, alias, null, null);
	}

	@Override
	public Pair<Environment, ContParser> parseDeferred(TypedAST first, CompilationContext ctx) {

		return null;
	}
}
