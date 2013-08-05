package wyvern.tools.parsing.extensions;

import java.util.HashSet;

import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.ToolError;
import wyvern.tools.parsing.*;
import wyvern.tools.rawAST.ExpressionSequence;
import wyvern.tools.rawAST.LineSequence;
import wyvern.tools.rawAST.Symbol;
import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.core.Sequence;
import wyvern.tools.typedAST.core.binding.TypeBinding;
import wyvern.tools.typedAST.core.declarations.DeclSequence;
import wyvern.tools.typedAST.core.declarations.TypeDeclaration;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.util.Pair;
import wyvern.tools.util.TreeWriter;

public class TypeParser implements DeclParser, TypeExtensionParser {
	private TypeParser() { }
	private static TypeParser instance = new TypeParser();
	public static TypeParser getInstance() { return instance; }
	
	public static class MutableTypeDeclaration extends TypeDeclaration {
		public MutableTypeDeclaration(String name, FileLocation location) {
			super(name, null, location);
		}
		
		public void setDecls(DeclSequence declSequence) {
			super.decls = declSequence;
		}

        public void setDeclEnv(Environment env) {
            super.declEnv.set(env);
        }
	}

	@Override
	public Pair<Environment, RecordTypeParser> parseRecord(TypedAST first,
														   Pair<ExpressionSequence, Environment> ctx) {
		Pair<Environment, ContParser> pair = parseDeferred(first, ctx);
		return new Pair<>(pair.first, (RecordTypeParser)pair.second);
	}

	@Override
	public boolean typeRequiredPartialParse(Pair<ExpressionSequence, Environment> ctx) {
		return false;
	}

	@Override
	public TypedAST parse(TypedAST first, Pair<ExpressionSequence, Environment> ctx) {
		Pair<Environment, ContParser> p = parseDeferred(first,  ctx);
		return p.second.parse(new ContParser.SimpleResolver(p.first.extend(ctx.second)));
	}
	
	@Override
	public Pair<Environment, ContParser> parseDeferred(TypedAST first,
			Pair<ExpressionSequence, Environment> ctx) {
		Symbol s = ParseUtils.parseSymbol(ctx);
		String clsName = s.name;
		FileLocation clsNameLine = s.getLocation();
		
		final MutableTypeDeclaration mtd = new MutableTypeDeclaration(clsName, clsNameLine);
		Environment newEnv = mtd.extend(ctx.second);
		newEnv = newEnv.extend(new TypeBinding("class", mtd.getType()));
		
		final LineSequence body = ParseUtils.extractLines(ctx);
		
		return new Pair<Environment,ContParser>(mtd.extend(Environment.getEmptyEnvironment()), new RecordTypeParser() {

            private Environment envin;
            private Environment envs;
            private Pair<Environment,ContParser> declAST;

			@Override
			public void parseTypes(EnvironmentResolver r) {
				Environment eEnv = r.getEnv(mtd);

				envin = mtd.extend(eEnv);
				envs = envin.extend(new TypeBinding("class", mtd.getType()));


				declAST = body.accept(ClassBodyParser.getInstance(), envs);
				mtd.setDeclEnv(declAST.first);
				if (declAST.second instanceof RecordTypeParser)
					((RecordTypeParser) declAST.second).parseTypes(r);
			}

			@Override
            public void parseInner(EnvironmentResolver r) {
				if (declAST.second instanceof RecordTypeParser)
					((RecordTypeParser) declAST.second).parseInner(r);
				mtd.setDeclEnv(((ClassBodyParser.ClassBodyContParser)declAST.second).internalEnv);
            }

            @Override
			public TypedAST parse(EnvironmentResolver r) {
				if (envin == null)
                    parseInner(r);
				final Environment envsf = envs.extend(declAST.first);
				
				TypedAST innerAST = declAST.second.parse(new EnvironmentResolver() {
					@Override
					public Environment getEnv(TypedAST elem) {
						return envsf;
					} 
				});
				

				if (!(innerAST instanceof Declaration) && !(innerAST instanceof Sequence))
					ToolError.reportError(ErrorMessage.UNEXPECTED_INPUT, innerAST);
				
				// Make sure that all members have unique names.
				HashSet<String> names = new HashSet<>();
				for (Declaration d : DeclSequence.getDeclSeq(innerAST).getDeclIterator()) {
					// System.out.println("Name " + d.getName() + " detected!");
					if (names.contains(d.getName())) {
						ToolError.reportError(ErrorMessage.DUPLICATE_MEMBER, mtd.getName(), d.getName(), mtd);
					}
					names.add(d.getName());
				}
				
				mtd.setDecls(DeclSequence.getDeclSeq(innerAST));
				return mtd;
			}
		
		});
		
	}
}
