package wyvern.tools.parsing.extensions;

import java.util.ArrayList;
import java.util.List;

import wyvern.tools.parsing.LineParser;
import wyvern.tools.parsing.ParseUtils;
import wyvern.tools.rawAST.ExpressionSequence;
import wyvern.tools.rawAST.Parenthesis;
import wyvern.tools.rawAST.RawAST;
import wyvern.tools.typedAST.TypedAST;
import wyvern.tools.typedAST.binding.NameBinding;
import wyvern.tools.typedAST.binding.NameBindingImpl;
import wyvern.tools.typedAST.extensions.Fn;
import wyvern.tools.typedAST.extensions.Meth;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.util.Pair;
import static wyvern.tools.parsing.ParseUtils.*;

/**
 * Parses "fn x : T => e"
 * 
 * Could specify as:   "fn" symbol ":" type "=>" exp
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
			ParseUtils.parseSymbol(":", newCtx);
			Type argType = ParseUtils.parseType(newCtx);
			NameBinding binding = new NameBindingImpl(argName, argType);
			ctx.second = ctx.second.extend(binding);
			args.add(binding);
		}
		
		if (returnType == null) {
			ParseUtils.parseSymbol(":", ctx);
			returnType = ParseUtils.parseType(ctx);
		}
		
		if (ctx.first == null)
			throw new RuntimeException("parse error");
			
		if (ParseUtils.checkFirst("=",ctx)) {
			ParseUtils.parseSymbol("=",ctx);
		}
		TypedAST exp = ParseUtils.parseExpr(ctx);					
		return new Meth(methName, args, returnType, exp);
		
	}
}
