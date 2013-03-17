package wyvern.tools.parsing;

import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.ToolError;
import wyvern.tools.rawAST.ExpressionSequence;
import wyvern.tools.rawAST.LineSequence;
import wyvern.tools.rawAST.Parenthesis;
import wyvern.tools.rawAST.RawAST;
import wyvern.tools.rawAST.Symbol;
import wyvern.tools.typedAST.TypedAST;
import wyvern.tools.typedAST.binding.TypeBinding;
import wyvern.tools.typedAST.extensions.Variable;
import wyvern.tools.typedAST.extensions.values.UnitVal;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.Arrow;
import wyvern.tools.types.extensions.Unit;
import wyvern.tools.util.Pair;

public class ParseUtils {

	public static RawAST peekFirst(Pair<ExpressionSequence, Environment> ctx) {
		if (ctx.first == null)
			return null;
			
		return ctx.first.getFirst();
	}
	
	public static boolean checkFirst(String string, Pair<ExpressionSequence, Environment> ctx) {
		RawAST first = peekFirst(ctx);
		return first != null && first instanceof Symbol && ((Symbol)first).name.equals(string);
	}

	public static Symbol parseSymbol(Pair<ExpressionSequence, Environment> ctx) {
		if (ctx.first == null) {
			ToolError.reportError(ErrorMessage.UNEXPECTED_INPUT, ctx.first);
		}
			
		RawAST first = ctx.first.getFirst();
		ExpressionSequence rest = ctx.first.getRest();
		ctx.first = rest;
		if (first instanceof Symbol) {
			return (Symbol) first;
		} else {
			ToolError.reportError(ErrorMessage.UNEXPECTED_INPUT, ctx.first);
			return null; // Unreachable.
		}
	}

	public static Parenthesis extractParen(Pair<ExpressionSequence, Environment> ctx) {
		if (ctx.first == null)
			ToolError.reportError(ErrorMessage.UNEXPECTED_INPUT, ctx.first);
			
		RawAST first = ctx.first.getFirst();
		ExpressionSequence rest = ctx.first.getRest();
		ctx.first = rest;
		if (first instanceof Parenthesis) {
			return (Parenthesis) first;
		} else {
			ToolError.reportError(ErrorMessage.UNEXPECTED_INPUT, ctx.first);
			return null; // Unreachable.
		}
	}

	public static LineSequence extractLines(Pair<ExpressionSequence, Environment> ctx) {
		if (ctx.first == null)
			ToolError.reportError(ErrorMessage.UNEXPECTED_INPUT, ctx.first);
			
		RawAST first = ctx.first.getFirst();
		ExpressionSequence rest = ctx.first.getRest();
		ctx.first = rest;
		if (first instanceof LineSequence) {
			return (LineSequence) first;
		} else {
			ToolError.reportError(ErrorMessage.UNEXPECTED_INPUT, ctx.first);
			return null; // Unreachable.
		}
	}

	public static Symbol parseSymbol(String string,
			Pair<ExpressionSequence, Environment> ctx) {
		Symbol symbol = parseSymbol(ctx);
		if (symbol.name.equals(string)) {
			return symbol;
		} else {
			ToolError.reportError(ErrorMessage.UNEXPECTED_INPUT, ctx.first);
			return null; // Unreachable.
		}
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
			ToolError.reportError(ErrorMessage.UNEXPECTED_INPUT, ctx.first);
			
		RawAST first = ctx.first.getFirst();
		ExpressionSequence rest = ctx.first.getRest();
		ctx.first = rest;
		if (first instanceof Symbol) {
			Symbol symbol = (Symbol) first;
			TypeBinding typeBinding = ctx.second.lookupType(symbol.name);
			
			// Take care of ?. Later properly parse the type parameters etc.
			if (checkFirst("?", ctx)) {
				parseSymbol("?", ctx); // Just ignore it for now. FIXME:
			}
			
			if (typeBinding == null) {
				// This should be picked up by symbol resolution in statically checked language!
				//	reportError(TYPE_NOT_DEFINED, symbol.name, symbol);
				typeBinding = new TypeBinding(symbol.name, Unit.getInstance()); // TODO: Create proper type representation.
			}
			
			return typeBinding.getUse();			
		} else if (first instanceof Parenthesis) {
			return parseType(new Pair<ExpressionSequence, Environment>((Parenthesis)first, ctx.second));
		} else {
			ToolError.reportError(ErrorMessage.UNEXPECTED_INPUT, ctx.first);
			return null; // Unreachable.
		}
	}

	// I do not think this method is needed!? (Alex) Why not use accept directly?
	public static TypedAST parseExpr(Pair<ExpressionSequence, Environment> ctx) {
		TypedAST result = ctx.first.accept(CoreParser.getInstance(), ctx.second);
		ctx.first = null;	// previous line by definition read everything
		return result;
	}

	public static Variable parseVariable(Pair<ExpressionSequence, Environment> ctx) {
		Symbol sym = parseSymbol(ctx);
		TypedAST var = sym.accept(CoreParser.getInstance(), ctx.second);
		if (!(var instanceof Variable))
			ToolError.reportError(ErrorMessage.UNEXPECTED_INPUT, ctx.first);
		return (Variable) var;
	}

	public static TypedAST parseExprList(Pair<ExpressionSequence, Environment> ctx) {
		if (ctx.first == null)
			ToolError.reportError(ErrorMessage.UNEXPECTED_INPUT, ctx.first);
		
		RawAST first = ctx.first.getFirst();
		ExpressionSequence rest = ctx.first.getRest();
		ctx.first = rest;
		if (first instanceof Parenthesis) {
			Parenthesis parens = (Parenthesis) first;
			if (parens.getFirst() != null)
				ToolError.reportError(ErrorMessage.UNEXPECTED_INPUT, ctx.first);
			// TODO: parse more than unit vals
			// maybe with parens.accept(CoreParser)?
			return UnitVal.getInstance(parens.getLocation());
		} else {
			ToolError.reportError(ErrorMessage.UNEXPECTED_INPUT, ctx.first);
			return null; // Unreachable.
		}
	}

}