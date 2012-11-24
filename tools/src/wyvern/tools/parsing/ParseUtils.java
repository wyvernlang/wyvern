package wyvern.tools.parsing;

import static wyvern.tools.errors.ErrorMessage.TYPE_NOT_DEFINED;
import static wyvern.tools.errors.ToolError.reportError;
import wyvern.tools.rawAST.ExpressionSequence;
import wyvern.tools.rawAST.Parenthesis;
import wyvern.tools.rawAST.RawAST;
import wyvern.tools.rawAST.Symbol;
import wyvern.tools.typedAST.TypedAST;
import wyvern.tools.typedAST.binding.TypeBinding;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.Arrow;
import wyvern.tools.util.Pair;

public class ParseUtils {

	public static Symbol parseSymbol(Pair<ExpressionSequence, Environment> ctx) {
		if (ctx.first == null)
			throw new RuntimeException("parse error");
			
		RawAST first = ctx.first.getFirst();
		ExpressionSequence rest = ctx.first.getRest();
		ctx.first = rest;
		if (first instanceof Symbol)
			return (Symbol) first;
		else
			throw new RuntimeException("parse error");
	}

	public static Symbol parseSymbol(String string,
			Pair<ExpressionSequence, Environment> ctx) {
		Symbol symbol = parseSymbol(ctx);
		if (symbol.name.equals(string))
			return symbol;
		else
			throw new RuntimeException("parse error");				
	}

	public static Type parseType(Pair<ExpressionSequence, Environment> ctx) {
		Type type = parseSimpleType(ctx);
		while (ctx.first != null && isArrowOperator(ctx.first.getFirst())) {
			ctx.first = ctx.first.getRest();
			Type argument = parseSimpleType(ctx);
			type = new Arrow(type, argument);
		}
		
		return type;
	}

	private static boolean isArrowOperator(RawAST operatorNode) {
		if (!(operatorNode instanceof Symbol))
			return false;
		String operatorName = ((Symbol) operatorNode).name;
		
		return operatorName.equals("->");
	}

	public static Type parseSimpleType(Pair<ExpressionSequence, Environment> ctx) {
		if (ctx.first == null)
			throw new RuntimeException("parse error");
			
		RawAST first = ctx.first.getFirst();
		ExpressionSequence rest = ctx.first.getRest();
		ctx.first = rest;
		if (first instanceof Symbol) {
			Symbol symbol = (Symbol) first;
			TypeBinding typeBinding = ctx.second.lookupType(symbol.name);
			if (typeBinding == null)
				reportError(TYPE_NOT_DEFINED, symbol.name, symbol);
			return typeBinding.getUse();			
		} else if (first instanceof Parenthesis)
			return parseType(new Pair<ExpressionSequence, Environment>((Parenthesis)first, ctx.second));
		else
			throw new RuntimeException("parse error");
	}

	public static TypedAST parseExpr(Pair<ExpressionSequence, Environment> ctx) {
		TypedAST result = ctx.first.accept(CoreParser.getInstance(), ctx.second);
		ctx.first = null;	// previous line by definition read everything
		return result;
	}

}
