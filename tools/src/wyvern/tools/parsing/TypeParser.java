package wyvern.tools.parsing;

import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.ToolError;
import wyvern.tools.rawAST.ExpressionSequence;
import wyvern.tools.rawAST.Parenthesis;
import wyvern.tools.rawAST.RawAST;
import wyvern.tools.rawAST.Symbol;
import wyvern.tools.typedAST.core.binding.TypeBinding;
import wyvern.tools.types.Environment;
import wyvern.tools.types.RecordType;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.Arrow;
import wyvern.tools.util.CompilationContext;
import wyvern.tools.util.Pair;

public class TypeParser {
	public static ParseUtils.LazyEval<Type> parsePartialType(CompilationContext ctx) {
		ParseUtils.LazyEval<Type> type = parseCompositeType(ctx);
		while (ctx.first != null && ParseUtils.isArrowOperator(ctx.first.getFirst())) {
			ctx.first = ctx.first.getRest();
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

	private static ParseUtils.LazyEval<Type> parseCompositeType(CompilationContext ctx) {
		ParseUtils.LazyEval<Type> type = parsePartialSimpleType(ctx);
		while (ctx.first != null && ParseUtils.checkFirst(".", ctx)) {
			final RawAST elem = ctx.first;
			ctx.first = ctx.first.getRest();
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
		if (ctx.first == null)
			ToolError.reportError(ErrorMessage.UNEXPECTED_INPUT, ctx.first);

		final RawAST first = ctx.first.getFirst();
		ExpressionSequence rest = ctx.first.getRest();
		ctx.first = rest;
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
						ToolError.reportError(ErrorMessage.TYPE_NOT_DEFINED, symbol.name, symbol);
						//typeBinding = new TypeBinding(symbol.name, null); // TODO: Create proper type representation.
					}

					return typeBinding.getUse();
				}

			};
		} else if (first instanceof Parenthesis) {
			return parsePartialType(new CompilationContext((Parenthesis) first, ctx.second));
		} else {
			ToolError.reportError(ErrorMessage.UNEXPECTED_INPUT, ctx.first);
			return null; // Unreachable.
		}
	}
}
