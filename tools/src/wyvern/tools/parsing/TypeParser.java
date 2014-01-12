package wyvern.tools.parsing;

import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.ToolError;
import wyvern.tools.rawAST.ExpressionSequence;
import wyvern.tools.rawAST.Parenthesis;
import wyvern.tools.rawAST.RawAST;
import wyvern.tools.rawAST.Symbol;
import wyvern.tools.typedAST.core.binding.TypeBinding;
import wyvern.tools.types.Environment;
import wyvern.tools.types.ParameterizableType;
import wyvern.tools.types.RecordType;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.Arrow;
import wyvern.tools.types.extensions.Tuple;
import wyvern.tools.util.CompilationContext;

import java.util.LinkedList;
import java.util.List;

public class TypeParser {
	public static ParseUtils.LazyEval<Type> parsePartialType(CompilationContext ctx) {
		ParseUtils.LazyEval<Type> type = parseTupleType(ctx);
		while (ctx.getTokens() != null && ParseUtils.isArrowOperator(ctx.getTokens().getFirst())) {
			ctx.setTokens(ctx.getTokens().getRest());
			final ParseUtils.LazyEval<Type> ctype = type;
			final ParseUtils.LazyEval<Type> argument = parsePartialType(ctx);
			type = new ParseUtils.LazyEval<Type>() {

				@Override
				public Type eval(Environment env) {
					return new Arrow(ctype.eval(env), argument.eval(env));
				}

			};
		}

		return type;
	}

	private static ParseUtils.LazyEval<Type> parseTupleType(CompilationContext ctx) {
		ParseUtils.LazyEval<Type> type = parseTypeParameter(ctx);
		while (ctx.getTokens() != null && ParseUtils.checkFirst("*", ctx)) {
			final RawAST elem = ctx.getTokens();
			ctx.setTokens(ctx.getTokens().getRest());
			final ParseUtils.LazyEval<Type> ptype = type;
			final ParseUtils.LazyEval<Type> ntype = parseCompositeType(ctx);
			type = new ParseUtils.LazyEval<Type>() {

				@Override
				public Type eval(Environment env) {
					Type iptype = ptype.eval(env);
					Type intype = ntype.eval(env);

					Type[] nt;
					if (iptype instanceof Tuple) {
						int length = ((Tuple) iptype).getTypes().length;
						nt = new Type[length + 1];
						System.arraycopy(((Tuple) iptype).getTypes(),0,nt,0,((Tuple) iptype).getTypes().length);
						nt[length] = intype;
					} else {
						nt = new Type[] { iptype, intype };
					}
					return new Tuple(nt);
				}

			};
		}
		return type;
	}

	private static ParseUtils.LazyEval<Type> parseTypeParameter(CompilationContext ctx) {
		final ParseUtils.LazyEval<Type> type = parseCompositeType(ctx);
		if (ParseUtils.checkFirst("[", ctx)) {
			final RawAST elem = ctx.getTokens();
			ParseUtils.parseSymbol("[",ctx);
			final LinkedList<ParseUtils.LazyEval<Type>> parameters = new LinkedList<>();
			do {
				parameters.add(parsePartialSimpleType(ctx));
			} while (ctx.getTokens() != null && ParseUtils.checkFirst(",", ctx));
			ParseUtils.parseSymbol("]",ctx);
			return new ParseUtils.LazyEval<Type>() {

				@Override
				public Type eval(Environment env) {
					Type pType = type.eval(env);
					if (!(pType instanceof ParameterizableType))
						ToolError.reportError(ErrorMessage.CANNOT_INVOKE, elem);
					List<Type> evaluated = new LinkedList<>();
					for (ParseUtils.LazyEval<Type> paramType : parameters)
						evaluated.add(paramType.eval(env));
					return ((ParameterizableType) pType).checkParameters(evaluated);
				}
			};
		}
		return type;
	}

	private static ParseUtils.LazyEval<Type> parseCompositeType(CompilationContext ctx) {
		ParseUtils.LazyEval<Type> type = parsePartialSimpleType(ctx);
		while (ctx.getTokens() != null && ParseUtils.checkFirst(".", ctx)) {
			final RawAST elem = ctx.getTokens();
			ctx.setTokens(ctx.getTokens().getRest());
			final ParseUtils.LazyEval<Type> ptype = type;
			final String prop = ParseUtils.parseSymbol(ctx).name;
			type = new ParseUtils.LazyEval<Type>() {

				@Override
				public Type eval(Environment env) {
					Type iptype = ptype.eval(env);
					if (!(iptype instanceof RecordType))
						ToolError.reportError(ErrorMessage.CANNOT_INVOKE, elem);

					return ((RecordType)iptype).getInnerType(prop);
				}

			};
		}
		return type;
	}

	public static ParseUtils.LazyEval<Type> parsePartialSimpleType(final CompilationContext ctx) {
		if (ctx.getTokens() == null)
			ToolError.reportError(ErrorMessage.UNEXPECTED_INPUT, ctx.getTokens());

		final RawAST first = ctx.getTokens().getFirst();
		ExpressionSequence rest = ctx.getTokens().getRest();
		ctx.setTokens(rest);
		if (first instanceof Symbol) {
			return new ParseUtils.LazyEval<Type>() {

				@Override
				public Type eval(Environment env) {
					Symbol symbol = (Symbol) first;
					TypeBinding typeBinding = env.lookupType(symbol.name);

					// Take care of ?. Later properly parse the type parameters etc.
					if (ParseUtils.checkFirst("?", ctx)) {
						ParseUtils.parseSymbol("?", ctx); // Just ignore it for now. FIXME:
					}

					if (typeBinding == null) {
						// This should be picked up by symbol resolution in statically checked language!
						ToolError.reportError(ErrorMessage.TYPE_NOT_DEFINED, symbol, symbol.name);
						//typeBinding = new TypeBinding(symbol.name, null); // TODO: Create proper type representation.
					}

					return typeBinding.getUse();
				}

			};
		} else if (first instanceof Parenthesis) {
			return parsePartialType(ctx.copyEnv((Parenthesis) first));
		} else {
			ToolError.reportError(ErrorMessage.UNEXPECTED_INPUT, ctx.getTokens());
			return null; // Unreachable.
		}
	}
}
