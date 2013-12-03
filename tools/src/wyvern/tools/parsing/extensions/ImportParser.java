package wyvern.tools.parsing.extensions;

import wyvern.DSL.DSL;
import wyvern.stdlib.*;
import wyvern.stdlib.Compiler;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.ToolError;
import wyvern.tools.parsing.*;
import wyvern.tools.parsing.resolvers.ImportEnvResolver;
import wyvern.tools.rawAST.StringLiteral;
import wyvern.tools.typedAST.core.declarations.ImportDeclaration;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.types.Environment;
import wyvern.tools.util.CompilationContext;
import wyvern.tools.util.Pair;
import wyvern.tools.util.Reference;

import javax.tools.Tool;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by Ben Chung on 8/9/13.
 */
public class ImportParser implements DeclParser {

	private static LineParser instance;

	public static LineParser getInstance() {
		if (instance == null)
			instance = new ImportParser();
		return instance;
	}
	private ImportParser() {}

	@Override
	public TypedAST parse(TypedAST first, CompilationContext ctx) {
		throw new RuntimeException();
	}

	private static class MutableImportDeclaration extends ImportDeclaration {

		public MutableImportDeclaration(String src, String equivName, ImportResolver res, FileLocation location) {
			super(src, equivName, null, res, location);
		}

		public void setEnv(Environment env) {
			super.setDeclEnv(env);
		}
	}

	@Override
	public Pair<Environment, ContParser> parseDeferred(TypedAST first, final CompilationContext ctx) {
		final StringLiteral importLiteral = (StringLiteral)ctx.popToken();
		final String importName = importLiteral.data;
		final URI uri = URI.create(importName);
		final ImportResolver resolver =
				ctx.getResolver().lookupReader(uri, new ArrayList<DSL>(), ctx, first);

		String alias = null;
		if (ParseUtils.checkFirst("as",ctx)) {
			ParseUtils.parseSymbol("as", ctx);
			alias = ParseUtils.parseSymbol(ctx).name;
		}
		final MutableImportDeclaration declaration = new MutableImportDeclaration(importName, alias, resolver, importLiteral.getLocation());
		if (resolver instanceof ImportEnvResolver) {
			declaration.setASTRef(new Reference<TypedAST>(((ImportEnvResolver)resolver).initalize(uri, new ArrayList<DSL>(), ctx)));
		}

		return new Pair<Environment, ContParser>(declaration.extend(Environment.getEmptyEnvironment()), new RecordTypeParser() {

			private Pair<Environment,ContParser> parserPair;
			private boolean parsed = false;

			@Override
			public TypedAST parse(EnvironmentResolver r) {
				if (parserPair == null) {
					parseTypes(r);
					parseInner(r);
				}
				if (parsed)
					return declaration;
				parsed = true;

				//Check for recursive call
				if (parserPair.second instanceof Compiler.CachingParser) {
					Compiler.CachingParser cachingParser = (Compiler.CachingParser) parserPair.second;
					if (cachingParser.recursiveCall()) {
						declaration.setASTRef(cachingParser.getRef());
						return declaration;
					}
				}

				TypedAST result = parserPair.second.parse(r);
				declaration.setASTRef(new Reference<TypedAST>(result));
                declaration.typecheck(Globals.getStandardEnv());
                declaration.evalDecl(Environment.getEmptyEnvironment());
				return declaration;
			}

			@Override
			public void parseTypes(EnvironmentResolver r) {
				try {
					parserPair = resolver.resolveImport(uri, new ArrayList<DSL>(), ctx);
				} catch (Exception e) {
					ToolError.reportError(ErrorMessage.UNEXPECTED_INPUT, importLiteral);
					throw new RuntimeException(e);
				}
				if (parserPair.second instanceof RecordTypeParser)
					((RecordTypeParser) parserPair.second).parseTypes(new SimpleResolver(Globals.getStandardEnv()));
				declaration.setEnv(parserPair.first);

			}

			@Override
			public void parseInner(EnvironmentResolver r) {
				if (parserPair.second instanceof RecordTypeParser)
					((RecordTypeParser) parserPair.second).parseInner(new SimpleResolver(Globals.getStandardEnv()));
			}
		});
	}
}
