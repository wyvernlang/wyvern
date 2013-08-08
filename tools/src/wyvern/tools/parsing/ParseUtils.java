package wyvern.tools.parsing;

import java.util.ArrayList;
import java.util.List;

import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.ToolError;
import wyvern.tools.rawAST.ExpressionSequence;
import wyvern.tools.rawAST.Line;
import wyvern.tools.rawAST.LineSequence;
import wyvern.tools.rawAST.Parenthesis;
import wyvern.tools.rawAST.RawAST;
import wyvern.tools.rawAST.Symbol;
import wyvern.tools.typedAST.core.binding.NameBinding;
import wyvern.tools.typedAST.core.binding.NameBindingImpl;
import wyvern.tools.typedAST.core.expressions.Variable;
import wyvern.tools.typedAST.core.values.UnitVal;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.Arrow;
import wyvern.tools.util.CompilationContext;
import wyvern.tools.util.Pair;

public class ParseUtils {

	public static RawAST peekFirst(CompilationContext ctx) {
		if (ctx.first == null)
			return null;
			
		return ctx.first.getFirst();
	}
	
	public static boolean checkFirst(String string, CompilationContext ctx) {
		RawAST first = peekFirst(ctx);
		return first != null && first instanceof Symbol && ((Symbol)first).name.equals(string);
	}

	public static Symbol parseSymbol(CompilationContext ctx) {
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

	public static Parenthesis extractParen(CompilationContext ctx) {
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

	public static LineSequence extractLines(CompilationContext ctx) {
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
			CompilationContext ctx) {
		Symbol symbol = parseSymbol(ctx);
		if (symbol.name.equals(string)) {
			return symbol;
		} else {
			ToolError.reportError(ErrorMessage.UNEXPECTED_INPUT, ctx.first);
			return null; // Unreachable.
		}
	}

	public static List<NameBinding> getNameBindings(CompilationContext ctx) {
		Parenthesis paren = ParseUtils.extractParen(ctx);
		CompilationContext newCtx = new CompilationContext(paren, ctx.second);
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
		return args;
	}

	public static Type parseReturnType(CompilationContext ctx) {
		Type returnType;
		ParseUtils.parseSymbol(":", ctx);
		returnType = ParseUtils.parseType(ctx);
		return returnType;
	}

	public interface LazyEval<T> {
		T eval(Environment env);
	}

	public static Type parseType(CompilationContext ctx) {
		return TypeParser.parsePartialType(ctx).eval(ctx.second);
		/*
		Type type = parseSimpleType(ctx);

		while (ctx.first != null && isArrowOperator(ctx.first.getFirst())) {
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
		return TypeParser.parsePartialSimpleType(ctx).eval(ctx.second);
	}

	// I do not think this method is needed!? (Alex) Why not use accept directly?
	public static TypedAST parseExpr(CompilationContext ctx) {
		TypedAST result = ctx.first.accept(BodyParser.getInstance(), ctx.second);
		ctx.first = null;	// previous line by definition read everything
		return result;
	}
	
	public static TypedAST parseCond(CompilationContext ctx) {
        return getLine(ctx).accept(BodyParser.getInstance(), ctx.second);
	}

    public static ExpressionSequence getLine(CompilationContext ctx) {
        ArrayList<RawAST> condRaw = new ArrayList<RawAST>();
        while (ctx.first != null && !(ctx.first.getFirst() instanceof LineSequence)) {
            condRaw.add(ctx.first.getFirst());
            ctx.first = ctx.first.getRest();
        }
        return new Line(condRaw, FileLocation.UNKNOWN);
    }

	public static Pair<Environment, ContParser> parseCondPartial(CompilationContext ctx) {
		ArrayList<RawAST> condRaw = new ArrayList<RawAST>();
		while (ctx.first != null && !(ctx.first.getFirst() instanceof LineSequence)) {
			condRaw.add(ctx.first.getFirst());
			ctx.first = ctx.first.getRest();
		}
		return new Line(condRaw, FileLocation.UNKNOWN).accept(DeclarationParser.getInstance(), ctx.second);
	}

	public static Variable parseVariable(CompilationContext ctx) {
		Symbol sym = parseSymbol(ctx);
		TypedAST var = sym.accept(BodyParser.getInstance(), ctx.second);
		if (!(var instanceof Variable))
			ToolError.reportError(ErrorMessage.UNEXPECTED_INPUT, ctx.first);
		return (Variable) var;
	}

	public static TypedAST parseExprList(CompilationContext ctx) {
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