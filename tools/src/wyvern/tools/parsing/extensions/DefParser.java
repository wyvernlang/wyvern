package wyvern.tools.parsing.extensions;

import java.util.ArrayList;
import java.util.List;

import wyvern.tools.errors.FileLocation;
import wyvern.tools.parsing.BodyParser;
import wyvern.tools.parsing.ContParser;
import wyvern.tools.parsing.DeclParser;
import wyvern.tools.parsing.ParseUtils;
import wyvern.tools.rawAST.ExpressionSequence;
import wyvern.tools.rawAST.Parenthesis;
import wyvern.tools.rawAST.Symbol;
import wyvern.tools.types.extensions.Unit;
import wyvern.tools.typedAST.core.binding.NameBinding;
import wyvern.tools.typedAST.core.binding.NameBindingImpl;
import wyvern.tools.typedAST.core.declarations.DefDeclaration;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.Arrow;
import wyvern.tools.util.Pair;
import static wyvern.tools.parsing.ParseUtils.*;

/**
 * Parses "def x : T => e"
 * 
 * Could specify as:   "def" symbol ":" type "=>" exp
 */

public class DefParser implements DeclParser {
	private DefParser() { }
	private static DefParser instance = new DefParser();
	public static DefParser getInstance() { return instance; }
	
	@Override
	public TypedAST parse(TypedAST first, Pair<ExpressionSequence, Environment> ctx) {
		return parse(first,ctx,false);
	}
	
	//REALLY HACKY (we don't have much of a choice, though)
	private static class MutableDefDeclaration extends DefDeclaration {
		public MutableDefDeclaration(String name, Type type, List<NameBinding> args, TypedAST body, boolean isClassMeth, FileLocation methNameLine) {
			super(name, type, args, body, isClassMeth, methNameLine);
		}
		
		public void setBody(TypedAST body) {
			this.body = body;
		}
	}
	
	public TypedAST parse(TypedAST first, Pair<ExpressionSequence,Environment> ctx, boolean isClassMeth) {
		Pair<Environment, ContParser> p = parseDeferred(first,  ctx, isClassMeth);
		return p.second.parse(new ContParser.SimpleResolver(p.first.extend(ctx.second)));
	}

	@Override
	public Pair<Environment, ContParser> parseDeferred(TypedAST first,
			Pair<ExpressionSequence, Environment> ctx) {
		return this.parseDeferred(first, ctx, false);
	}
	
	// FIXME: Should convert all functions: f (A, B) : C into f : A*B -> C and thus convert f() : C into f : Unit -> C!
	public Pair<Environment, ContParser> parseDeferred(TypedAST first,
			Pair<ExpressionSequence, Environment> ctx, final boolean isClassMeth) {
		Symbol s = ParseUtils.parseSymbol(ctx);
		final String defName = s.name;
		final FileLocation methNameLine = s.getLocation();
		final Type returnType;
		
		final List<NameBinding> args = new ArrayList<NameBinding>();
		Environment argsEnv = Environment.getEmptyEnvironment();
		boolean argumentsPresent = false;
		
		if (ParseUtils.peekFirst(ctx) != null &&
			ParseUtils.peekFirst(ctx) instanceof Parenthesis) { // () present
			
			argumentsPresent = true;
			Parenthesis paren = ParseUtils.extractParen(ctx);
			Pair<ExpressionSequence,Environment> newCtx = new Pair<ExpressionSequence,Environment>(paren, ctx.second); 

			while (newCtx.first != null && !newCtx.first.children.isEmpty()) {
				if (args.size() > 0)
					ParseUtils.parseSymbol(",", newCtx);
					
				String argName = ParseUtils.parseSymbol(newCtx).name;
				
				Type argType = null;
				if (ParseUtils.checkFirst(":", newCtx)) {
					argType = parseReturnType(newCtx);
				} else {
					// What's wrong with no type for arg? Seems allowed...
				}
				NameBinding binding = new NameBindingImpl(argName, argType);
				argsEnv = argsEnv.extend(binding);
				args.add(binding);
			}
		}
		
		final Environment savedArgsEnv = argsEnv;
		
		if (ParseUtils.checkFirst(":", ctx)) {
			returnType = parseReturnType(ctx);
		} else {
			returnType = wyvern.tools.types.extensions.Unit.getInstance();
		}
		
		// Process body now.
		final ExpressionSequence exp;
		int type = 0;
		
		if (ctx.first == null) {
			// Empty body is OK - say inside type.
			exp = null;
		} else if (ParseUtils.checkFirst("=",ctx)) {
			ParseUtils.parseSymbol("=",ctx);
			exp = ctx.first;
		} else {
			exp = ctx.first;
		}
		
		
		ctx.first = null; // don't forget to reset!
		
		final Type defType;
		if (argumentsPresent) {
			defType = DefDeclaration.getMethodType(args, returnType);
		} else {
			defType = returnType;
		}
		final MutableDefDeclaration md = new MutableDefDeclaration(defName, defType,
				args, null, isClassMeth, methNameLine);
		
		return new Pair<Environment, ContParser>(md.extend(Environment.getEmptyEnvironment()), new ContParser() {

			@Override
			public TypedAST parse(EnvironmentResolver envR) {
				Environment env = envR.getEnv(md);
				TypedAST inExp;
				DefDeclaration iMD = md;
				
				if (exp == null) {
					inExp = null;
				} else {
					inExp = exp.accept(BodyParser.getInstance(), env.extend(savedArgsEnv));
				}
				md.setBody(inExp);

				return new DefDeclaration(defName, defType,
						args, inExp, isClassMeth, methNameLine);
			}
			
		});
		
	}
}