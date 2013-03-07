package wyvern.tools.parsing.extensions;

import java.util.ArrayList;
import java.util.List;

import wyvern.tools.parsing.CoreParser;
import wyvern.tools.parsing.LineParser;
import wyvern.tools.parsing.ParseUtils;
import wyvern.tools.rawAST.ExpressionSequence;
import wyvern.tools.rawAST.Line;
import wyvern.tools.rawAST.LineSequence;
import wyvern.tools.rawAST.Parenthesis;
import wyvern.tools.rawAST.RawAST;
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
		return parse(first,ctx,null);
	}
	
	public TypedAST parse(TypedAST first, Pair<ExpressionSequence,Environment> ctx, Type returnType) {
		String methName = ParseUtils.parseSymbol(ctx).name;
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
		
		TypedAST exp;
		if (ctx.first == null) {
			// Empty body is OK - say inside interface.
			exp = null;
		} else if (ParseUtils.checkFirst("=",ctx)) {
			ParseUtils.parseSymbol("=",ctx);
			exp = ParseUtils.parseExpr(ctx);					
		} else {
			// Multiple line body then! FIXME: This is not working properly!
			// System.out.println("Multiline: " + methName + " and returntype is " + returnType + " and ctx = " + ctx.first);
			exp = ParseUtils.parseExpr(ctx);					
		}

		return new MethDeclaration(methName, args, returnType, exp);
		
	}
}
