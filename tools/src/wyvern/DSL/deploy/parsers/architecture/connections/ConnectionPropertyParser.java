package wyvern.DSL.deploy.parsers.architecture.connections;

import wyvern.tools.parsing.*;
import wyvern.tools.types.Environment;
import wyvern.tools.util.CompilationContext;
import wyvern.tools.util.Pair;

public abstract class ConnectionPropertyParser implements DeclParser {
	public Pair<Environment, ContParser> iParse(CompilationContext ctx) {
		if (ctx.getTokens() != null) {
			Pair<Environment, ContParser> ast =
					ParseUtils.extractLines(ctx).accept(new DeclarationParser(ctx), ctx.getEnv());
			ctx.setTokens(null);//The last line ate the rest already
			return ast;
		}
		return null;
	}
}
