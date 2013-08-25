package wyvern.targets.JavaScript.parsers;

import wyvern.targets.JavaScript.typedAST.JSCast;
import wyvern.tools.parsing.LineParser;
import wyvern.tools.parsing.ParseUtils;
import wyvern.tools.rawAST.ExpressionSequence;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.util.CompilationContext;
import wyvern.tools.util.Pair;


public class JSCastParser implements LineParser {

	@Override
	public TypedAST parse(TypedAST first,
						  CompilationContext ctx) {
		Type resultType = ParseUtils.parseType(ctx);
		ParseUtils.parseSymbol("in", ctx);
		TypedAST body = ParseUtils.parseExpr(ctx);

		return new JSCast(body, resultType, body.getLocation());
	}
}
