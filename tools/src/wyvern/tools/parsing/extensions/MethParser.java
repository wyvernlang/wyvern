package wyvern.tools.parsing.extensions;

import java.util.ArrayList;
import java.util.List;

import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.ToolError;
import wyvern.tools.parsing.BodyParser;
import wyvern.tools.parsing.ContParser;
import wyvern.tools.parsing.DeclParser;
import wyvern.tools.parsing.LineParser;
import wyvern.tools.parsing.ParseUtils;
import wyvern.tools.rawAST.ExpressionSequence;
import wyvern.tools.rawAST.Line;
import wyvern.tools.rawAST.LineSequence;
import wyvern.tools.rawAST.Parenthesis;
import wyvern.tools.rawAST.RawAST;
import wyvern.tools.rawAST.Symbol;
import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.core.binding.NameBinding;
import wyvern.tools.typedAST.core.binding.NameBindingImpl;
import wyvern.tools.typedAST.core.declarations.MethDeclaration;
import wyvern.tools.typedAST.core.expressions.Fn;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.util.Pair;
import static wyvern.tools.parsing.ParseUtils.*;

/**
 * Parses "meth x : T => e"
 * 
 * Could specify as:   "meth" symbol ":" type "=>" exp
 */

public class MethParser implements DeclParser {
	private MethParser() { }
	private static MethParser instance = new MethParser();
	public static MethParser getInstance() { return instance; }
	
	@Override
	public TypedAST parse(TypedAST first, Pair<ExpressionSequence,Environment> ctx) {
		return parse(first,ctx,null,false);
	}
	
	//REALLY HACKY (to get recursive methods for now until refactoring is done)
	private static class MutableMethDeclaration extends MethDeclaration {
		public MutableMethDeclaration(String name, List<NameBinding> args, Type returnType, TypedAST body, boolean isClassMeth, FileLocation methNameLine) {
			super(name, args, returnType, body, isClassMeth, methNameLine);
		}
		
		public void setBody(TypedAST body) {
			this.body = body;
		}
	}
	
	public TypedAST parse(TypedAST first, Pair<ExpressionSequence,Environment> ctx, Type returnType, boolean isClassMeth) {
		Symbol s = ParseUtils.parseSymbol(ctx);
		String methName = s.name;
		FileLocation methNameLine = s.getLocation();
		
		Parenthesis paren = ParseUtils.extractParen(ctx);
		Pair<ExpressionSequence,Environment> newCtx = new Pair<ExpressionSequence,Environment>(paren, ctx.second); 
		List<NameBinding> args = new ArrayList<NameBinding>();

		while (newCtx.first != null && !newCtx.first.children.isEmpty()) {
			if (args.size() > 0)
				ParseUtils.parseSymbol(",", newCtx);
				
			String argName = ParseUtils.parseSymbol(newCtx).name;
			
			Type argType = null;
			if (ParseUtils.checkFirst(":", newCtx)) {
				ParseUtils.parseSymbol(":", newCtx);
				argType = ParseUtils.parseType(newCtx);
			} else {
				// What's wrong with no type for arg? Seems allowed...
			}
			NameBinding binding = new NameBindingImpl(argName, argType);
			ctx.second = ctx.second.extend(binding);
			args.add(binding);
		}
		
		if (ParseUtils.checkFirst(":", ctx)) {
			if (returnType == null) {
				ParseUtils.parseSymbol(":", ctx);
				returnType = ParseUtils.parseType(ctx);
			}
		} else {
			returnType = wyvern.tools.types.extensions.Unit.getInstance();
		}
		
		// Process body now.
		TypedAST exp = null;
		MutableMethDeclaration md = new MutableMethDeclaration(methName, args, returnType, null, isClassMeth, methNameLine);
		
		if (ctx.first == null) {
			// Empty body is OK - say inside interface.
			exp = null;
		} else if (ParseUtils.checkFirst("=",ctx)) {
			ParseUtils.parseSymbol("=",ctx);
			exp = ctx.first.accept(BodyParser.getInstance(), md.extend(ctx.second));
			ctx.first = null; // don't forget to reset!
		} else {
			exp = ctx.first.accept(BodyParser.getInstance(), md.extend(ctx.second));
			ctx.first = null; // don't forget to reset!
		}
		
		md.setBody(exp);

		return new MethDeclaration(methName, args, returnType, exp, isClassMeth, methNameLine); // Discard mutable md... hack...
		
	}

	@Override
	public Pair<Environment, ContParser> parseDeferred(TypedAST first,
			Pair<ExpressionSequence, Environment> ctx) {
		return this.parseDeferred(first, ctx, false);
		
	}
	
	public Pair<Environment, ContParser> parseDeferred(TypedAST first,
			Pair<ExpressionSequence, Environment> ctx, final boolean isClassMeth) {
		Symbol s = ParseUtils.parseSymbol(ctx);
		final String methName = s.name;
		final FileLocation methNameLine = s.getLocation();
		final Type returnType;
		
		Parenthesis paren = ParseUtils.extractParen(ctx);
		Pair<ExpressionSequence,Environment> newCtx = new Pair<ExpressionSequence,Environment>(paren, ctx.second); 
		final List<NameBinding> args = new ArrayList<NameBinding>();
		Environment argsEnv = Environment.getEmptyEnvironment();

		while (newCtx.first != null && !newCtx.first.children.isEmpty()) {
			if (args.size() > 0)
				ParseUtils.parseSymbol(",", newCtx);
				
			String argName = ParseUtils.parseSymbol(newCtx).name;
			
			Type argType = null;
			if (ParseUtils.checkFirst(":", newCtx)) {
				ParseUtils.parseSymbol(":", newCtx);
				argType = ParseUtils.parseType(newCtx);
			} else {
				// What's wrong with no type for arg? Seems allowed...
			}
			NameBinding binding = new NameBindingImpl(argName, argType);
			argsEnv = argsEnv.extend(binding);
			args.add(binding);
		}
		
		final Environment savedArgsEnv = argsEnv;
		
		if (ParseUtils.checkFirst(":", ctx)) {
			ParseUtils.parseSymbol(":", ctx);
			returnType = ParseUtils.parseType(ctx);
		} else {
			returnType = wyvern.tools.types.extensions.Unit.getInstance();
		}
		
		// Process body now.
		final ExpressionSequence exp;
		int type = 0;
		
		if (ctx.first == null) {
			// Empty body is OK - say inside interface.
			exp = null;
		} else if (ParseUtils.checkFirst("=",ctx)) {
			ParseUtils.parseSymbol("=",ctx);
			exp = ctx.first;
		} else {
			exp = ctx.first;
		}
		
		
		ctx.first = null; // don't forget to reset!
		
		final MutableMethDeclaration md = new MutableMethDeclaration(methName, args, returnType, null, isClassMeth, methNameLine);
		
		return new Pair<Environment, ContParser>(md.extend(Environment.getEmptyEnvironment()), new ContParser() {

			@Override
			public TypedAST parse(EnvironmentResolver envR) {
				Environment env = envR.getEnv(md);
				TypedAST inExp;
				MethDeclaration iMD = md;
				if (exp == null) {
					inExp = null;
				} else {
					inExp = exp.accept(BodyParser.getInstance(), env.extend(savedArgsEnv));
				}
				md.setBody(inExp);

				return new MethDeclaration(methName, args, returnType, inExp, isClassMeth, methNameLine);
			}
			
		});
		
	}
}