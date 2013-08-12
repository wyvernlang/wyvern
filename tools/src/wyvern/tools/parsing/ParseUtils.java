package wyvern.tools.parsing;

import java.util.ArrayList;
import java.util.List;

import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.ToolError;
import wyvern.tools.rawAST.*;
import wyvern.tools.typedAST.core.binding.NameBinding;
import wyvern.tools.typedAST.core.binding.NameBindingImpl;
import wyvern.tools.typedAST.core.expressions.Variable;
import wyvern.tools.typedAST.core.values.UnitVal;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.util.CompilationContext;
import wyvern.tools.util.Pair;

public class ParseUtils {

	public static RawAST peekFirst(CompilationContext ctx) {
		if (ctx.getTokens() == null)
			return null;
			
		return ctx.getTokens().getFirst();
	}
	
	public static boolean checkFirst(String string, CompilationContext ctx) {
		RawAST first = peekFirst(ctx);
		return first != null && first instanceof Symbol && ((Symbol)first).name.equals(string);
	}

	public static Symbol parseSymbol(CompilationContext ctx) {
		if (ctx.getTokens() == null) {
			ToolError.reportError(ErrorMessage.UNEXPECTED_INPUT, ctx.getTokens());
		}
			
		RawAST first = ctx.getTokens().getFirst();
		ExpressionSequence rest = ctx.getTokens().getRest();
		ctx.setTokens(rest);
		if (first instanceof Symbol) {
			return (Symbol) first;
		} else {
			ToolError.reportError(ErrorMessage.UNEXPECTED_INPUT, ctx.getTokens());
			return null; // Unreachable.
		}
	}

	public static Parenthesis extractParen(CompilationContext ctx) {
		if (ctx.getTokens() == null)
			ToolError.reportError(ErrorMessage.UNEXPECTED_INPUT, ctx.getTokens());
			
		RawAST first = ctx.getTokens().getFirst();
		ExpressionSequence rest = ctx.getTokens().getRest();
		ctx.setTokens(rest);
		if (first instanceof Parenthesis) {
			return (Parenthesis) first;
		} else {
			ToolError.reportError(ErrorMessage.UNEXPECTED_INPUT, ctx.getTokens());
			return null; // Unreachable.
		}
	}

	public static LineSequence extractLines(CompilationContext ctx) {
		if (ctx.getTokens() == null)
			ToolError.reportError(ErrorMessage.UNEXPECTED_INPUT, ctx.getTokens());
			
		RawAST first = ctx.getTokens().getFirst();
		ExpressionSequence rest = ctx.getTokens().getRest();
		ctx.setTokens(rest);
		if (first instanceof LineSequence) {
			return (LineSequence) first;
		} else {
			ToolError.reportError(ErrorMessage.UNEXPECTED_INPUT, ctx.getTokens());
			return null; // Unreachable.
		}
	}

	public static Symbol parseSymbol(String string,
			CompilationContext ctx) {
		Symbol symbol = parseSymbol(ctx);
		if (symbol.name.equals(string)) {
			return symbol;
		} else {
			ToolError.reportError(ErrorMessage.UNEXPECTED_INPUT, ctx.getTokens());
			return null; // Unreachable.
		}
	}

	public static List<NameBinding> getNameBindings(CompilationContext ctx) {
		Parenthesis paren = ParseUtils.extractParen(ctx);
		CompilationContext newCtx = ctx.copyEnv(paren);
		List<NameBinding> args = new ArrayList<NameBinding>();

		while (newCtx.getTokens() != null && !newCtx.getTokens().children.isEmpty()) {
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
			ctx.setEnv(ctx.getEnv().extend(binding));
			args.add(binding);
		}
		return args;
	}

	public static Type parseReturnType(CompilationContext ctx) {
		Type returnType;
		ParseUtils.parseSymbol(":", ctx);
		returnType = ParseUtils.parseType(ctx);
		return returnType;
	}

	public static String parseStringLiteral(CompilationContext ctx) {
		RawAST elem = peekFirst(ctx);
		if (!(elem instanceof StringLiteral))
			return null;
		return ((StringLiteral) elem).data;
	}

	public interface LazyEval<T> {
		T eval(Environment env);
	}

	public static Type parseType(CompilationContext ctx) {
		return TypeParser.parsePartialType(ctx).eval(ctx.getEnv());
		/*
		Type type = parseSimpleType(ctx);

		while (ctx.first != null && isArrowOperator(ctx.first.getTokens())) {
			ctx.first = ctx.first.getRest();
			Type argument = parseType(ctx);
			type = new Arrow(type, argument);
		}
		
		return type;
		*/
	}

	public static boolean isArrowOperator(RawAST operatorNode) {
		if (!(operatorNode instanceof Symbol))
			return false;
		String operatorName = ((Symbol) operatorNode).name;
		
		return operatorName.equals("->");
	}

	public static Type parseSimpleType(CompilationContext ctx) {
		return TypeParser.parsePartialSimpleType(ctx).eval(ctx.getEnv());
	}

	// I do not think this method is needed!? (Alex) Why not use accept directly?
	public static TypedAST parseExpr(CompilationContext ctx) {
		TypedAST result = ctx.getTokens().accept(BodyParser.getInstance(), ctx.getEnv());
		ctx.setTokens(null);	// previous line by definition read everything
		return result;
	}
	
	public static TypedAST parseCond(CompilationContext ctx) {
        return getLine(ctx).accept(BodyParser.getInstance(), ctx.getEnv());
	}

    public static ExpressionSequence getLine(CompilationContext ctx) {
        ArrayList<RawAST> condRaw = new ArrayList<RawAST>();
        while (ctx.getTokens() != null && !(ctx.getTokens().getFirst() instanceof LineSequence)) {
            condRaw.add(ctx.getTokens().getFirst());
            ctx.setTokens(ctx.getTokens().getRest());
        }
        return new Line(condRaw, FileLocation.UNKNOWN);
    }

	public static Pair<Environment, ContParser> parseCondPartial(CompilationContext ctx) {
		ArrayList<RawAST> condRaw = new ArrayList<RawAST>();
		while (ctx.getTokens() != null && !(ctx.getTokens().getFirst() instanceof LineSequence)) {
			condRaw.add(ctx.getTokens().getFirst());
			ctx.setTokens(ctx.getTokens().getRest());
		}
		return new Line(condRaw, FileLocation.UNKNOWN).accept(DeclarationParser.getInstance(), ctx.getEnv());
	}

	public static Variable parseVariable(CompilationContext ctx) {
		Symbol sym = parseSymbol(ctx);
		TypedAST var = sym.accept(BodyParser.getInstance(), ctx.getEnv());
		if (!(var instanceof Variable))
			ToolError.reportError(ErrorMessage.UNEXPECTED_INPUT, ctx.getTokens());
		return (Variable) var;
	}

	public static TypedAST parseExprList(CompilationContext ctx) {
		if (ctx.getTokens() == null)
			ToolError.reportError(ErrorMessage.UNEXPECTED_INPUT, ctx.getTokens());
		
		RawAST first = ctx.getTokens().getFirst();
		ExpressionSequence rest = ctx.getTokens().getRest();
		ctx.setTokens(rest);
		if (first instanceof Parenthesis) {
			Parenthesis parens = (Parenthesis) first;
			if (parens.getFirst() != null)
				ToolError.reportError(ErrorMessage.UNEXPECTED_INPUT, ctx.getTokens());
			// TODO: parse more than unit vals
			// maybe with parens.accept(CoreParser)?
			return UnitVal.getInstance(parens.getLocation());
		} else {
			ToolError.reportError(ErrorMessage.UNEXPECTED_INPUT, ctx.getTokens());
			return null; // Unreachable.
		}
	}

}