package wyvern.tools.parsing.extensions;

import java.util.HashSet;

import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.ToolError;
import wyvern.tools.parsing.BodyParser;
import wyvern.tools.parsing.ContParser;
import wyvern.tools.parsing.DeclParser;
import wyvern.tools.parsing.DeclarationParser;
import wyvern.tools.parsing.ParseUtils;
import wyvern.tools.rawAST.ExpressionSequence;
import wyvern.tools.rawAST.LineSequence;
import wyvern.tools.rawAST.Symbol;
import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.core.Sequence;
import wyvern.tools.typedAST.core.binding.TypeBinding;
import wyvern.tools.typedAST.core.declarations.DeclSequence;
import wyvern.tools.typedAST.core.declarations.TypeDeclaration;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.types.Environment;
import wyvern.tools.util.Pair;

public class TypeParser implements DeclParser {
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
		
		return new Pair<Environment,ContParser>(mtd.extend(Environment.getEmptyEnvironment()), new ContParser() {

			@Override
			public TypedAST parse(EnvironmentResolver r) {
				Environment eEnv = r.getEnv(mtd);

				Environment envin = mtd.extend(eEnv); 
				final Environment envs = envin.extend(new TypeBinding("class", mtd.getType()));

				final Pair<Environment,ContParser> declAST = body.accept(DeclarationParser.getInstance(), envs);
				
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
