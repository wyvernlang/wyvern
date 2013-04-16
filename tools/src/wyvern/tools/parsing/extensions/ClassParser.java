package wyvern.tools.parsing.extensions;

import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.ToolError;
import wyvern.tools.parsing.BodyParser;
import wyvern.tools.parsing.ContParser.EnvironmentResolver;
import wyvern.tools.parsing.DeclParser;
import wyvern.tools.parsing.DeclarationParser;
import wyvern.tools.parsing.LineParser;
import wyvern.tools.parsing.ParseUtils;
import wyvern.tools.parsing.ContParser;
import wyvern.tools.rawAST.ExpressionSequence;
import wyvern.tools.rawAST.LineSequence;
import wyvern.tools.rawAST.Symbol;
import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.core.Sequence;
import wyvern.tools.typedAST.core.binding.NameBindingImpl;
import wyvern.tools.typedAST.core.binding.TypeBinding;
import wyvern.tools.typedAST.core.declarations.ClassDeclaration;
import wyvern.tools.typedAST.core.declarations.DeclSequence;
import wyvern.tools.typedAST.core.declarations.MethDeclaration;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.types.Environment;
import wyvern.tools.types.extensions.ClassType;
import wyvern.tools.types.extensions.Unit;
import wyvern.tools.util.Pair;

/**
 * 	class NAME
 * 		[implements NAME]
 *		[class implements NAME]
 * 		DELCARATION*
 */

public class ClassParser implements DeclParser {
	private ClassParser() { }
	private static ClassParser instance = new ClassParser();
	public static ClassParser getInstance() { return instance; }
	
	//REALLY HACKY
	private static class MutableClassDeclaration extends ClassDeclaration {
		public MutableClassDeclaration(String name, String implementsName,
				String implementsClassName, FileLocation clsNameLine) {
			super(name, implementsName, implementsClassName, null, clsNameLine);
		}

		public void setDecls(DeclSequence decl) {
			this.decls = decl;
		}
		
		
	}

	@Override
	public TypedAST parse(TypedAST first, Pair<ExpressionSequence,Environment> ctx) {
		if (ParseUtils.checkFirst("meth", ctx)) { // Parses "class meth".
			ParseUtils.parseSymbol(ctx);
			return MethParser.getInstance().parse(first, ctx, null, true);
		}
		
		Symbol s = ParseUtils.parseSymbol(ctx);
		String clsName = s.name;
		FileLocation clsNameLine = s.getLocation();

		TypedAST declAST = null;
		String implementsName = "";
		String implementsClassName = "";
		MutableClassDeclaration mutableDecl = new MutableClassDeclaration(clsName, implementsName, implementsClassName, clsNameLine);

		if (ctx.first == null) {
			return mutableDecl;
		}
		
		LineSequence lines = ParseUtils.extractLines(ctx); // Get potential body.
		
		if (lines.getFirst() != null && lines.getFirst().getFirst() != null &&
				lines.getFirst().getFirst().toString().equals("implements")) { // FIXME: hack, detected implements
			implementsName = lines.getFirst().getRest().getFirst().toString();
			lines.children.remove(0);
		}

		if (lines.getFirst() != null && lines.getFirst().getFirst() != null &&
				lines.getFirst().getFirst().toString().equals("class")) { // FIXME: hack, detected class
			if (lines.getFirst().getRest() != null && lines.getFirst().getRest().getFirst() != null &&
					lines.getFirst().getRest().getFirst().toString().equals("implements")) { // FIXME: hack, detected implements
				implementsClassName = lines.getFirst().getRest().getRest().getFirst().toString();
				lines.children.remove(0);
			}
		}

		// Process body.
		if (ctx.first != null)
			ToolError.reportError(ErrorMessage.UNEXPECTED_INPUT, ctx.first);
		
		mutableDecl = new MutableClassDeclaration(clsName, implementsName, implementsClassName, clsNameLine);
		
		Environment newEnv = mutableDecl.extend(ctx.second); 
		newEnv = newEnv.extend(new TypeBinding("class", mutableDecl.getType()));
		final Pair<Environment,ContParser> declASTParser = lines.accept(DeclarationParser.getInstance(), newEnv);
		
		final Environment envs = newEnv;
		final Environment envi = newEnv.extend(new NameBindingImpl("this", mutableDecl.getType()));
		
		declAST = declASTParser.second.parse(new EnvironmentResolver() {
			@Override
			public Environment getEnv(TypedAST elem) {
				if (elem instanceof MethDeclaration && ((MethDeclaration) elem).isClassMeth()) {
						return envs;
				}
				return envi;
			} 
		});
		
		if (!(declAST instanceof Declaration) && !(declAST instanceof Sequence))
			ToolError.reportError(ErrorMessage.UNEXPECTED_INPUT, ctx.first);
		
		mutableDecl.setDecls(DeclSequence.getDeclSeq(declAST));
		

		return mutableDecl;
	}

	@Override
	public Pair<Environment, ContParser> parseDeferred(TypedAST first,
			Pair<ExpressionSequence, Environment> ctx) {
		if (ParseUtils.checkFirst("meth", ctx)) { // Parses "class meth".
			ParseUtils.parseSymbol(ctx);
			return MethParser.getInstance().parseDeferred(first, ctx, true);
		}
		
		Symbol s = ParseUtils.parseSymbol(ctx);
		String clsName = s.name;
		FileLocation clsNameLine = s.getLocation();

		String implementsName = "";
		String implementsClassName = "";

		final MutableClassDeclaration mutableDecl = new MutableClassDeclaration(clsName, implementsName, implementsClassName, clsNameLine);
		
		if (ctx.first == null) {
			return new Pair<Environment,ContParser>(mutableDecl.extend(Environment.getEmptyEnvironment()),new ContParser() {
				@Override
				public TypedAST parse(EnvironmentResolver env) {
					return mutableDecl;
				}
			});
		}
		
		LineSequence lines = ParseUtils.extractLines(ctx); // Get potential body.
		
		if (lines.getFirst() != null && lines.getFirst().getFirst() != null &&
				lines.getFirst().getFirst().toString().equals("implements")) { // FIXME: hack, detected implements
			implementsName = lines.getFirst().getRest().getFirst().toString();
			lines.children.remove(0);
		}

		if (lines.getFirst() != null && lines.getFirst().getFirst() != null &&
				lines.getFirst().getFirst().toString().equals("class")) { // FIXME: hack, detected class
			if (lines.getFirst().getRest() != null && lines.getFirst().getRest().getFirst() != null &&
					lines.getFirst().getRest().getFirst().toString().equals("implements")) { // FIXME: hack, detected implements
				implementsClassName = lines.getFirst().getRest().getRest().getFirst().toString();
				lines.children.remove(0);
			}
		}

		// Process body.
		if (ctx.first != null)
			ToolError.reportError(ErrorMessage.UNEXPECTED_INPUT, ctx.first);
		
		final MutableClassDeclaration mutableDeclf = new MutableClassDeclaration(clsName, implementsName, implementsClassName, clsNameLine);
		
		Environment newEnv = mutableDeclf.extend(Environment.getEmptyEnvironment()); 
		
		Environment typecheckEnv = ctx.second.extend(newEnv);
		typecheckEnv = typecheckEnv.extend(new TypeBinding("class", mutableDeclf.getType()));
		
		final Pair<Environment,ContParser> declAST = lines.accept(DeclarationParser.getInstance(), typecheckEnv);
		
		return new Pair<Environment,ContParser>(newEnv, new ContParser() {

			@Override
			public TypedAST parse(EnvironmentResolver envR) {
				Environment external = envR.getEnv(mutableDeclf);
				
				Environment envin = mutableDeclf.extend(external); 
				final Environment envs = envin.extend(new TypeBinding("class", mutableDeclf.getType()));
				final Environment envi = envs.extend(new NameBindingImpl("this", mutableDeclf.getType()));
				
				TypedAST innerAST = declAST.second.parse(new EnvironmentResolver() {
					@Override
					public Environment getEnv(TypedAST elem) {
						if (elem instanceof MethDeclaration && ((MethDeclaration) elem).isClassMeth()) {
								return envs;
						}
						return envi;
					} 
				});
				
				if (!(innerAST instanceof Declaration) && !(innerAST instanceof Sequence))
					ToolError.reportError(ErrorMessage.UNEXPECTED_INPUT, innerAST);
				
				mutableDeclf.setDecls(DeclSequence.getDeclSeq(innerAST));
				

				return mutableDeclf;
			}
			
		});
	}
}
