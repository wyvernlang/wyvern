package wyvern.tools.parsing.extensions;

import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.ToolError;
import wyvern.tools.parsing.BodyParser;
import wyvern.tools.parsing.ContParser;
import wyvern.tools.parsing.DeclParser;
import wyvern.tools.parsing.DeclarationParser;
import wyvern.tools.parsing.LineParser;
import wyvern.tools.parsing.ParseUtils;
import wyvern.tools.parsing.ContParser.EnvironmentResolver;
import wyvern.tools.rawAST.ExpressionSequence;
import wyvern.tools.rawAST.LineSequence;
import wyvern.tools.rawAST.Symbol;
import wyvern.tools.typedAST.Declaration;
import wyvern.tools.typedAST.Sequence;
import wyvern.tools.typedAST.TypedAST;
import wyvern.tools.typedAST.binding.NameBindingImpl;
import wyvern.tools.typedAST.binding.TypeBinding;
import wyvern.tools.typedAST.extensions.DeclSequence;
import wyvern.tools.typedAST.extensions.declarations.MethDeclaration;
import wyvern.tools.typedAST.extensions.declarations.TypeDeclaration;
import wyvern.tools.types.Environment;
import wyvern.tools.util.Pair;

public class TypeParser implements DeclParser {
	private TypeParser() { }
	private static TypeParser instance = new TypeParser();
	public static TypeParser getInstance() { return instance; }
	
	private class MutableTypeDeclaration extends TypeDeclaration {
		public MutableTypeDeclaration(String name, FileLocation location) {
			super(name, null, location);
		}
		
		public void setDecls(DeclSequence declSequence) {
			super.decls = declSequence;
		}
	}

	@Override
	public TypedAST parse(TypedAST first, Pair<ExpressionSequence,Environment> ctx) {
		Symbol s = ParseUtils.parseSymbol(ctx);
		String clsName = s.name;
		FileLocation clsNameLine = s.getLocation();

		TypedAST declAST = null;
		if (ctx.first == null) {
			// Empty body in the interface declaration is OK.
		} else {
			// Process body.
			LineSequence lines = ParseUtils.extractLines(ctx);
			if (ctx.first != null)
				ToolError.reportError(ErrorMessage.UNEXPECTED_INPUT, ctx.first);

			declAST = lines.accept(BodyParser.getInstance(), ctx.second);
		}

		return new TypeDeclaration(clsName, DeclSequence.getDeclSeq(declAST), clsNameLine);
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
		

		final Pair<Environment,ContParser> declAST = ParseUtils.extractLines(ctx).accept(DeclarationParser.getInstance(), newEnv);
		
		return new Pair<Environment,ContParser>(mtd.extend(Environment.getEmptyEnvironment()), new ContParser() {

			@Override
			public TypedAST parse(EnvironmentResolver r) {
				Environment eEnv = r.getEnv(mtd);

				Environment envin = mtd.extend(eEnv); 
				final Environment envs = envin.extend(new TypeBinding("class", mtd.getType()));
				TypedAST innerAST = declAST.second.parse(new EnvironmentResolver() {
					@Override
					public Environment getEnv(TypedAST elem) {
						return envs;
					} 
				});
				

				if (!(innerAST instanceof Declaration) && !(innerAST instanceof Sequence))
					ToolError.reportError(ErrorMessage.UNEXPECTED_INPUT, innerAST);
				
				mtd.setDecls(DeclSequence.getDeclSeq(innerAST));
				return mtd;
			}
		
		});
		
	}
}
