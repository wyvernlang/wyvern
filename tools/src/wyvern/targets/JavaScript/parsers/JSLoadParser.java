package wyvern.targets.JavaScript.parsers;

import wyvern.targets.JavaScript.typedAST.JSFunction;
import wyvern.tools.parsing.LineParser;
import wyvern.tools.parsing.ParseUtils;
import wyvern.tools.rawAST.ExpressionSequence;
import wyvern.tools.typedAST.core.Application;
import wyvern.tools.typedAST.core.Invocation;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.Str;
import wyvern.tools.util.Pair;
import static wyvern.tools.types.TypeUtils.arrow;

public class JSLoadParser implements LineParser {

	@Override
	public TypedAST parse(TypedAST first,
			Pair<ExpressionSequence, Environment> ctx) {
		Type resultType = ParseUtils.parseType(ctx);
		ParseUtils.parseSymbol("in", ctx);
		TypedAST body = ParseUtils.parseExpr(ctx);
		
		return new Application(new JSFunction(arrow(Str.getInstance(),resultType), "require"), body, first.getLocation());
	}

}
