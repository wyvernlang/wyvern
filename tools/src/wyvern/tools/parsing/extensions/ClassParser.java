package wyvern.tools.parsing.extensions;

import java.util.HashSet;
import java.util.LinkedList;

import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.ToolError;
import wyvern.tools.parsing.ContParser.EnvironmentResolver;
import wyvern.tools.parsing.DeclParser;
import wyvern.tools.parsing.DeclarationParser;
import wyvern.tools.parsing.ParseUtils;
import wyvern.tools.parsing.ContParser;
import wyvern.tools.rawAST.ExpressionSequence;
import wyvern.tools.rawAST.LineSequence;
import wyvern.tools.rawAST.Symbol;
import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.core.Sequence;
import wyvern.tools.typedAST.core.binding.ClassBinding;
import wyvern.tools.typedAST.core.binding.NameBinding;
import wyvern.tools.typedAST.core.binding.NameBindingImpl;
import wyvern.tools.typedAST.core.binding.TypeBinding;
import wyvern.tools.typedAST.core.declarations.*;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.Arrow;
import wyvern.tools.types.extensions.ClassType;
import wyvern.tools.types.extensions.TypeType;
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
				String implementsClassName, Environment declEnv, FileLocation clsNameLine) {
			super(name, implementsName, implementsClassName, null, declEnv, clsNameLine);
		}

        public void setDeclEnv(Environment nd) {
            super.declEnvRef.set(nd);
        }

		public void setDecls(DeclSequence decl) {
			this.decls = decl;
			updateEnv();
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
		if (ParseUtils.checkFirst("def", ctx)) { // Parses "class def". // FIXME: Should this connect to the keyword in Globals?
			ParseUtils.parseSymbol(ctx);
			return DefParser.getInstance().parseDeferred(first, ctx, true);
		}
		
		Symbol s = ParseUtils.parseSymbol(ctx);
		String clsName = s.name;
		FileLocation clsNameLine = s.getLocation();

		String implementsName = "";
		String implementsClassName = "";

		final MutableClassDeclaration mutableDecl = new MutableClassDeclaration(clsName, implementsName, implementsClassName, null, clsNameLine);

		if (ctx.first == null) {
			return new Pair<Environment,ContParser>(mutableDecl.extend(Environment.getEmptyEnvironment()),new ContParser() {
                @Override
                public void parseInner(EnvironmentResolver r) {

                }

                @Override
				public TypedAST parse(EnvironmentResolver env) {
					return mutableDecl;
				}
			});
		}
		
		final LineSequence lines = ParseUtils.extractLines(ctx); // Get potential body.
		
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
		
		final MutableClassDeclaration mutableDeclf = new MutableClassDeclaration(clsName, implementsName, implementsClassName, null, clsNameLine);
		
		Environment newEnv = mutableDeclf.extend(Environment.getEmptyEnvironment()); 
		
		Environment typecheckEnv = ctx.second.extend(newEnv);
		typecheckEnv = typecheckEnv.extend(new ClassBinding("class", mutableDeclf));
		
		
		return new Pair<Environment,ContParser>(newEnv, new ContParser() {

            private Environment envs = null;
            private Environment envi;
            private Pair<Environment,ContParser> declAST;

            @Override
            public void parseInner(EnvironmentResolver envR) {
                Environment external = envR.getEnv(mutableDeclf);

                Environment envin = mutableDeclf.extend(external);
                envs = envin.extend(new ClassBinding("class", mutableDeclf));
                declAST = lines.accept(DeclarationParser.getInstance(), envs);
                envi = envs.extend(new NameBindingImpl("this", mutableDeclf.getType()));
                mutableDeclf.setDeclEnv(declAST.first);
            }

            @Override
			public TypedAST parse(EnvironmentResolver envR) {
                if (envs == null)
                    parseInner(envR);
				TypedAST innerAST = declAST.second.parse(new EnvironmentResolver() {
					@Override
					public Environment getEnv(TypedAST elem) {
						if (elem instanceof DefDeclaration && ((DefDeclaration) elem).isClass()) {
								return envs;
						}
						return envi;
					}
				});
				
				if (!(innerAST instanceof Declaration) && !(innerAST instanceof Sequence))
					ToolError.reportError(ErrorMessage.UNEXPECTED_INPUT, innerAST);
				
				// Make sure that all members have unique names.
				HashSet<String> names = new HashSet<>();
				for (Declaration d : DeclSequence.getDeclSeq(innerAST).getDeclIterator()) {
					if (names.contains(d.getName())) {
						ToolError.reportError(ErrorMessage.DUPLICATE_MEMBER, mutableDeclf.getName(), d.getName(), mutableDeclf);
					}
					names.add(d.getName());
				}
				
				mutableDeclf.setDecls(DeclSequence.getDeclSeq(innerAST));

				return mutableDeclf;
			}
			
		});
	}
}
