package wyvern.tools.parsing.extensions;

import java.util.ArrayList;
import java.util.List;

import wyvern.tools.errors.FileLocation;
import wyvern.tools.parsing.CoreParser;
import wyvern.tools.parsing.LineParser;
import wyvern.tools.parsing.ParseUtils;
import wyvern.tools.rawAST.ExpressionSequence;
import wyvern.tools.rawAST.Line;
import wyvern.tools.rawAST.LineSequence;
import wyvern.tools.rawAST.Parenthesis;
import wyvern.tools.rawAST.RawAST;
import wyvern.tools.rawAST.Symbol;
import wyvern.tools.typedAST.Declaration;
import wyvern.tools.typedAST.TypedAST;
import wyvern.tools.typedAST.binding.NameBinding;
import wyvern.tools.typedAST.binding.NameBindingImpl;
import wyvern.tools.typedAST.extensions.Fn;
import wyvern.tools.typedAST.extensions.declarations.MethDeclaration;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.util.Pair;
import static wyvern.tools.parsing.ParseUtils.*;

/**
 * Parses "meth x : T => e"
 * 
 * Could specify as:   "meth" symbol ":" type "=>" exp
 */

public class MethParser implements LineParser {
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
			// What's wrong with no return type? Seems allowed...
		}
		
		// Process body now.
		TypedAST exp = null;
		MutableMethDeclaration md = new MutableMethDeclaration(methName, args, returnType, null, isClassMeth, methNameLine);
		
		if (ctx.first == null) {
			// Empty body is OK - say inside interface.
			exp = null;
		} else if (ParseUtils.checkFirst("=",ctx)) {
			ParseUtils.parseSymbol("=",ctx);
			exp = ctx.first.accept(CoreParser.getInstance(), md.extend(ctx.second));
			ctx.first = null; // don't forget to reset!
		} else {
			exp = ctx.first.accept(CoreParser.getInstance(), md.extend(ctx.second));
			ctx.first = null; // don't forget to reset!
		}
		
		md.setBody(exp);

		return new MethDeclaration(methName, args, returnType, exp, isClassMeth, methNameLine); // Discard mutable md... hack...
		
	}
}