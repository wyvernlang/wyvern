package wyvern.DSL.deploy.parsers.architecture.connections;

import wyvern.tools.parsing.*;
import wyvern.tools.rawAST.ExpressionSequence;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.types.Environment;
import wyvern.tools.util.Pair;

public abstract class ConnectionPropertyParser implements DeclParser {
	public Pair<Environment, ContParser> iParse(Pair<ExpressionSequence, Environment> ctx) {
		if (ctx.first != null) {
			Pair<Environment, ContParser> ast =
					ParseUtils.extractLines(ctx).accept(DeclarationParser.getInstance(), ctx.second);
			ctx.first = null;//The last line ate the rest already
			return ast;
		}
		return null;
	}
}
