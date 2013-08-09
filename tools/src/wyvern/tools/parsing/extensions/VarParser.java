package wyvern.tools.parsing.extensions;

import static wyvern.tools.parsing.ParseUtils.parseSymbol;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.ToolError;
import wyvern.tools.parsing.ContParser;
import wyvern.tools.parsing.DeclParser;
import wyvern.tools.parsing.ParseUtils;
import wyvern.tools.typedAST.core.binding.NameBindingImpl;
import wyvern.tools.typedAST.core.declarations.VarDeclaration;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.util.CompilationContext;
import wyvern.tools.util.Pair;

public class VarParser implements DeclParser {
	private VarParser() { }
	private static VarParser instance = new VarParser();
	public static VarParser getInstance() { return instance; }
	
	@Override
	public TypedAST parse(TypedAST first, CompilationContext ctx) {
		Pair<Environment, ContParser> p = parseDeferred(first,  ctx);
		return p.second.parse(new ContParser.SimpleResolver(p.first.extend(ctx.getEnv())));
	}


	@Override
	public Pair<Environment, ContParser> parseDeferred(TypedAST first,
			final CompilationContext ctx) {
		final String varName = ParseUtils.parseSymbol(ctx).name;
		
		if (ParseUtils.checkFirst("=", ctx)) {
			ToolError.reportError(ErrorMessage.UNEXPECTED_INPUT, ctx.getTokens());
			return null;	
		} else if (ParseUtils.checkFirst(":", ctx)) {
			parseSymbol(":", ctx);
			final Type parsedType = ParseUtils.parseType(ctx);
			
			final CompilationContext restctx = ctx.copyAndClear();
			
			final VarDeclaration intermvd = new VarDeclaration(varName, parsedType, null);
			
			return new Pair<Environment, ContParser>(Environment.getEmptyEnvironment().extend(new NameBindingImpl(varName, parsedType)), new ContParser(){
                @Override
				public TypedAST parse(EnvironmentResolver r) {
					if (restctx.getTokens() == null)
						return new VarDeclaration(varName, parsedType, null);
					else if (ParseUtils.checkFirst("=", restctx)) {
						ParseUtils.parseSymbol("=", restctx);
						CompilationContext ctxi = new CompilationContext(restctx.getTokens(), r.getEnv(intermvd));
						return new VarDeclaration(varName, parsedType, ParseUtils.parseExpr(restctx));
					} else {
						ToolError.reportError(ErrorMessage.UNEXPECTED_INPUT, ctx.getTokens());
						return null;
					}
				}});
		} else {
			ToolError.reportError(ErrorMessage.UNEXPECTED_INPUT, ctx.getTokens());
			return null;
		}
	}
}