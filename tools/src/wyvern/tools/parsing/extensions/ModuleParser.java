package wyvern.tools.parsing.extensions;

import wyvern.DSL.DSL;
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

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class ModuleParser implements DeclParser {
    private static Dictionary<String, Pair<Environment, ContParser>> resolved = new Hashtable<>();
    private static LineParser instance;

    public static LineParser getInstance() {
        if (instance == null)
            instance = new ModuleParser();
        return instance;
    }
    private ModuleParser() {}

    private ModuleDeclaration.ImportDeclaration parseImport(Line line) {
		String importName = "";
		while (line != null) {
			RawAST elem = line.getFirst();
			if (elem instanceof Symbol)
			    importName += ((Symbol) elem).name;
            else if (elem instanceof IntLiteral)
                importName += ((IntLiteral) elem).data;
            else
                break;
            line = line.getRest();
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
            line = line.getRest();

			out.add(parseImport(line));

			lines.set(lines.get().getRest());
		}

		return out;
	}

    private Pair<Environment,RecordTypeParser> getImportedEnvironment(List<ModuleDeclaration.ImportDeclaration> imports) {
        Environment start = Environment.getEmptyEnvironment();
        final List<ContParser> imported = new LinkedList<>();
        for (ModuleDeclaration.ImportDeclaration importDeclaration : imports) {
            if (resolved.get(importDeclaration.getSrc()) == null) {
                try {
                    Pair<Environment, ContParser> pair = wyvern.stdlib.Compiler
                            .compilePartial(new URI(importDeclaration.getSrc()), new ArrayList<DSL>());
                    resolved.put(importDeclaration.getSrc(), pair);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }
            Pair<Environment, ContParser> environmentContParserPair = resolved.get(importDeclaration.getSrc());
            start = start.extend(environmentContParserPair.first);
            imported.add(environmentContParserPair.second);
        }

        return new Pair<Environment, RecordTypeParser>(start, new RecordTypeParser() {

            @Override
            public void parseTypes(EnvironmentResolver r) {
                for (ContParser parser : imported)
                    if (parser instanceof RecordTypeParser)
                        ((RecordTypeParser) parser).parseTypes(r);
            }

            @Override
            public void parseInner(EnvironmentResolver r) {
                for (ContParser parser : imported)
                    if (parser instanceof RecordTypeParser)
                        ((RecordTypeParser) parser).parseInner(r);
            }

            @Override
            public TypedAST parse(EnvironmentResolver r) {
                throw new RuntimeException("Invalid operation");
            }
        });
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
		final List<ModuleDeclaration.ImportDeclaration> imports = parseImports(lines);


		final MutableModuleDeclaration mutableDecl =
				new MutableModuleDeclaration(name, imports, s.getLocation());

        Environment newEnv = mutableDecl.extend(Environment.getEmptyEnvironment());
        Pair<Environment,ContParser> result = new Pair<>(newEnv, null);
        resolved.put(name, result);

        if (ctx.first == null) {
            result.second = new ContParser() {

				@Override
				public TypedAST parse(EnvironmentResolver env) {
					return mutableDecl;
				}
			};
		}

        result.second = new RecordTypeParser.RecordTypeParserBase() {
			public Pair<Environment, ContParser> declAST;
			public Environment env;
			private Pair<Environment, RecordTypeParser> imported;

			@Override
			public void doParseTypes(EnvironmentResolver r) {
				if (mutableDecl.getDeclEnv() != null)
					return;
				imported = getImportedEnvironment(imports);
				env = imported.first.extend(r.getEnv(mutableDecl));
				declAST = lines.get().accept(ClassBodyParser.getInstance(), env);
				if (declAST.second instanceof RecordTypeParser)
					((RecordTypeParser)declAST.second).parseTypes(new SimpleResolver(env));
				mutableDecl.setDeclEnv(declAST.first);
				imported.second.parseTypes(r);
			}

			@Override
			public void doParseInner(EnvironmentResolver r) {
				if (declAST.second instanceof RecordTypeParser)
					((RecordTypeParser)declAST.second).parseInner(new SimpleResolver(env));
				mutableDecl.setDeclEnv(((ClassBodyParser.ClassBodyContParser)declAST.second).getInternalEnv());
				imported.second.parseInner(r);
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
		};
        return result;
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

		public Environment getDeclEnv() {
			return super.declEnv.get();
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
