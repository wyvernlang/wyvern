package wyvern.tools.parsing.extensions;

import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.ToolError;
import wyvern.tools.parsing.*;
import wyvern.tools.parsing.TypeParser;
import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.core.*;
import wyvern.tools.typedAST.core.declarations.DeclSequence;
import wyvern.tools.typedAST.core.declarations.DefDeclaration;
import wyvern.tools.typedAST.core.declarations.ModuleDeclaration;
import wyvern.tools.rawAST.*;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.TypeDeclUtils;
import wyvern.tools.util.Pair;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class ModuleParser implements DeclParser {

	private ModuleDeclaration.ImportDeclaration parseImport(Line line) {
		String importName = "";
		while (line != null) {
			RawAST elem = line.getFirst();
			if (!(elem instanceof Symbol))
				break;

			importName += ((Symbol) elem).name;
		}
		return new ModuleDeclaration.ImportDeclaration(importName);
	}

	private List<ModuleDeclaration.ImportDeclaration> parseImports(AtomicReference<LineSequence> lines) {
		LinkedList<ModuleDeclaration.ImportDeclaration> out = new LinkedList<>();
		while (lines.get() != null) {
			Line line = lines.get().getFirst();

			RawAST first = line.getFirst();

			if (!(first instanceof Symbol))
				break;

			Symbol firstSym = (Symbol)first;

			if (!firstSym.name.equals("import"))
				break;

			out.add(parseImport(lines.get().getFirst()));

			lines.set(lines.get().getRest());
		}

		return out;
	}

	//parses "module name : t i d
	@Override
	public Pair<Environment, ContParser> parseDeferred(TypedAST first, Pair<ExpressionSequence, Environment> ctx) {

		Symbol s = ParseUtils.parseSymbol(ctx);
		String name = s.name;
		ParseUtils.LazyEval<Type> asc = null;
		if (ParseUtils.checkFirst(":", ctx)) {
			ParseUtils.parseSymbol(":", ctx);
			asc = TypeParser.parsePartialType(ctx);
		}
		final AtomicReference<LineSequence> lines = new AtomicReference<>(ParseUtils.extractLines(ctx));
		List<ModuleDeclaration.ImportDeclaration> imports = parseImports(lines);


		final MutableModuleDeclaration mutableDecl =
				new MutableModuleDeclaration(name, imports, s.getLocation());

		if (ctx.first == null) {
			return new Pair<Environment,ContParser>(mutableDecl.extend(Environment.getEmptyEnvironment()),new ContParser() {

				@Override
				public TypedAST parse(EnvironmentResolver env) {
					return mutableDecl;
				}
			});
		}

		return new Pair<Environment,ContParser>(mutableDecl.extend(Environment.getEmptyEnvironment()), new RecordTypeParser() {
			public Pair<Environment, ContParser> declAST;
			public Environment env;

			@Override
			public void parseTypes(EnvironmentResolver r) {
				env = r.getEnv(mutableDecl);
				declAST = lines.get().accept(ClassBodyParser.getInstance(), env);
				if (declAST.second instanceof RecordTypeParser)
					((RecordTypeParser)declAST.second).parseTypes(new SimpleResolver(env));
				mutableDecl.setDeclEnv(declAST.first);
			}

			@Override
			public void parseInner(EnvironmentResolver r) {
				if (declAST.second instanceof RecordTypeParser)
					((RecordTypeParser)declAST.second).parseInner(new SimpleResolver(env));
				mutableDecl.setDeclEnv(((ClassBodyParser.ClassBodyContParser)declAST.second).getInternalEnv());
			}

			@Override
			public TypedAST parse(EnvironmentResolver r) {
				if (env == null) {
					parseTypes(r);
					parseInner(r);
				}

				TypedAST innerAST = declAST.second.parse(new SimpleResolver(env));

				if (!(innerAST instanceof Declaration) && !(innerAST instanceof wyvern.tools.typedAST.core.Sequence))
					ToolError.reportError(ErrorMessage.UNEXPECTED_INPUT, innerAST);

				// Make sure that all members have unique names.
				HashSet<String> names = new HashSet<>();
				for (Declaration d : DeclSequence.getDeclSeq(innerAST).getDeclIterator()) {
					if (names.contains(d.getName())) {
						ToolError.reportError(ErrorMessage.DUPLICATE_MEMBER, mutableDecl.getName(), d.getName(), mutableDecl);
					}
					names.add(d.getName());
				}

				mutableDecl.setDecls(DeclSequence.getDeclSeq(innerAST));
				return mutableDecl;
			}
		});
	}

	@Override
	public TypedAST parse(TypedAST first, Pair<ExpressionSequence, Environment> ctx) {
		Pair<Environment, ContParser> ret = parseDeferred(first, ctx);
		return ret.second.parse(new ContParser.SimpleResolver(ret.first.extend(ctx.second)));
	}

	public static class MutableModuleDeclaration extends ModuleDeclaration {
		public MutableModuleDeclaration(String name, List<ImportDeclaration> imports, FileLocation location) {
			super(name, null, location);
		}

		public void setDeclEnv(Environment nd) {
			super.declEnv.set(nd);
		}

		public void setDecls(DeclSequence decl) {
			this.decls = decl;
			typeEquivalentEnvironment.set(TypeDeclUtils.getTypeEquivalentEnvironment(getDecls(), false));
		}
	}
}
