package wyvern.tools.parsing.extensions;

import static wyvern.tools.parsing.ParseUtils.parseSymbol;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.ToolError;
import wyvern.tools.parsing.ContParser;
import wyvern.tools.parsing.DeclParser;
import wyvern.tools.parsing.ParseUtils;
import wyvern.tools.rawAST.ExpressionSequence;
import wyvern.tools.typedAST.core.binding.NameBindingImpl;
import wyvern.tools.typedAST.core.declarations.ValDeclaration;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.util.Pair;

public class ValParser implements DeclParser {
	private ValParser() { }
	private static ValParser instance = new ValParser();
	public static ValParser getInstance() { return instance; }
	

	@Override
	public TypedAST parse(TypedAST first, Pair<ExpressionSequence, Environment> ctx) {
		String varName = ParseUtils.parseSymbol(ctx).name;
		
		if (!ParseUtils.checkFirst(":", ctx)) {
			ToolError.reportError(ErrorMessage.UNEXPECTED_INPUT, ctx.first);
			return null;
		}
		parseSymbol(":", ctx);
		Type type = ParseUtils.parseType(ctx);
		
		if (ParseUtils.checkFirst("=", ctx)) {
			parseSymbol("=", ctx);
			TypedAST exp = ParseUtils.parseExpr(ctx);
			return new ValDeclaration(varName, type, exp);	
		} else if (ctx.first == null) {
			return new ValDeclaration(varName, type, null);
		} else {
			ToolError.reportError(ErrorMessage.UNEXPECTED_INPUT, ctx.first);
			return null;
		}
	}


	//@Override
	public Pair<Environment, ContParser> parseDeferred(TypedAST first,
			final Pair<ExpressionSequence, Environment> ctx) {
		final String varName = ParseUtils.parseSymbol(ctx).name;
		
		if (ParseUtils.checkFirst("=", ctx)) {
			//ToolError.reportError(ErrorMessage.UNEXPECTED_INPUT, ctx.first);
			//return null;
		}
		Type type = null;
		if (ParseUtils.checkFirst(":", ctx)) {
			parseSymbol(":", ctx);
			type = ParseUtils.parseType(ctx);
		}
			
		final Pair<ExpressionSequence, Environment> restctx = new Pair<ExpressionSequence,Environment>(ctx.first,ctx.second);
		ctx.first = null;

		ValDeclaration nc = null;
		if (restctx.first == null)
			nc = new ValDeclaration(varName, type, null);
		else if (ParseUtils.checkFirst("=", restctx)) {
			ParseUtils.parseSymbol("=", restctx);
			Pair<ExpressionSequence,Environment> ctxi = new Pair<ExpressionSequence,Environment>(restctx.first, ctx.second);
			TypedAST definition = ParseUtils.parseExpr(restctx);
			if (type == null)
				type = definition.typecheck(ctx.second);
			nc = new ValDeclaration(varName, type, definition);
		} else {
			ToolError.reportError(ErrorMessage.UNEXPECTED_INPUT, restctx.first);
			nc = null;
		}
		final Type parsedType = type;

		final ValDeclaration intermvd = nc;
		return new Pair<Environment, ContParser>(Environment.getEmptyEnvironment().extend(new NameBindingImpl(varName, parsedType)), new ContParser(){

			@Override
			public TypedAST parse(EnvironmentResolver r) {
				return intermvd;
			}});
	}
}