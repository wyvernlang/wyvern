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
import wyvern.tools.typedAST.core.binding.TypeBinding;
import wyvern.tools.typedAST.core.expressions.Variable;
import wyvern.tools.typedAST.core.values.UnitVal;
import wyvern.tools.typedAST.interfaces.TypedAST;
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

	public static List<NameBinding> getNameBindings(Pair<ExpressionSequence, Environment> ctx) {
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
		return args;
	}

	public static Type parseReturnType(Pair<ExpressionSequence, Environment> ctx) {
		Type returnType;
		ParseUtils.parseSymbol(":", ctx);
		returnType = ParseUtils.parseType(ctx);
		return returnType;
	}

	public interface LazyEval<T> {
		T eval(Environment env);
	}
	
	public static LazyEval<Type> parsePartialType(Pair<ExpressionSequence, Environment> ctx) {

		LazyEval<Type> type = parsePartialSimpleType(ctx);
		while (ctx.first != null && isArrowOperator(ctx.first.getFirst())) {
			ctx.first = ctx.first.getRest();
			final LazyEval<Type> ctype = type;
			final LazyEval<Type> argument = parsePartialType(ctx);
			type = new LazyEval<Type>() {

				@Override
				public Type eval(Environment env) {
					return new Arrow(ctype.eval(env), argument.eval(env));
				}
				
			};
		}
		
		return type;
	}

	public static Type parseType(Pair<ExpressionSequence, Environment> ctx) {
		Type type = parseSimpleType(ctx);

		while (ctx.first != null && isArrowOperator(ctx.first.getFirst())) {
			ctx.first = ctx.first.getRest();
			Type argument = parseType(ctx);
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
	
	public static LazyEval<Type> parsePartialSimpleType(final Pair<ExpressionSequence, Environment> ctx) {
		if (ctx.first == null)
			ToolError.reportError(ErrorMessage.UNEXPECTED_INPUT, ctx.first);
			
		final RawAST first = ctx.first.getFirst();
		ExpressionSequence rest = ctx.first.getRest();
		ctx.first = rest;
		if (first instanceof Symbol) {
			return new LazyEval<Type>() {

				@Override
				public Type eval(Environment env) {
					Symbol symbol = (Symbol) first;
					TypeBinding typeBinding = env.lookupType(symbol.name);
					
					// Take care of ?. Later properly parse the type parameters etc.
					if (checkFirst("?", ctx)) {
						parseSymbol("?", ctx); // Just ignore it for now. FIXME:
					}
					
					if (typeBinding == null) {
						// This should be picked up by symbol resolution in statically checked language!
						ToolError.reportError(ErrorMessage.TYPE_NOT_DEFINED, symbol.name, symbol);
						//typeBinding = new TypeBinding(symbol.name, null); // TODO: Create proper type representation.
					}
					
					return typeBinding.getUse();
				}
				
			};			
		} else if (first instanceof Parenthesis) {
			return parsePartialType(new Pair<ExpressionSequence, Environment>((Parenthesis)first, ctx.second));
		} else {
			ToolError.reportError(ErrorMessage.UNEXPECTED_INPUT, ctx.first);
			return null; // Unreachable.
		}
	}
	
	public static Type parseSimpleType(Pair<ExpressionSequence, Environment> ctx) {
		return parsePartialSimpleType(ctx).eval(ctx.second);
	}

	// I do not think this method is needed!? (Alex) Why not use accept directly?
	public static TypedAST parseExpr(Pair<ExpressionSequence, Environment> ctx) {
		TypedAST result = ctx.first.accept(BodyParser.getInstance(), ctx.second);
		ctx.first = null;	// previous line by definition read everything
		return result;
	}
	
	public static TypedAST parseCond(Pair<ExpressionSequence, Environment> ctx) {
        return getLine(ctx).accept(BodyParser.getInstance(), ctx.second);
	}

    public static ExpressionSequence getLine(Pair<ExpressionSequence, Environment> ctx) {
        ArrayList<RawAST> condRaw = new ArrayList<RawAST>();
        while (ctx.first != null && !(ctx.first.getFirst() instanceof LineSequence)) {
            condRaw.add(ctx.first.getFirst());
            ctx.first = ctx.first.getRest();
        }
        return new Line(condRaw, FileLocation.UNKNOWN);
    }

	public static Pair<Environment, ContParser> parseCondPartial(Pair<ExpressionSequence, Environment> ctx) {
		ArrayList<RawAST> condRaw = new ArrayList<RawAST>();
		while (ctx.first != null && !(ctx.first.getFirst() instanceof LineSequence)) {
			condRaw.add(ctx.first.getFirst());
			ctx.first = ctx.first.getRest();
		}
		return new Line(condRaw, FileLocation.UNKNOWN).accept(DeclarationParser.getInstance(), ctx.second);
	}

	public static Variable parseVariable(Pair<ExpressionSequence, Environment> ctx) {
		Symbol sym = parseSymbol(ctx);
		TypedAST var = sym.accept(BodyParser.getInstance(), ctx.second);
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