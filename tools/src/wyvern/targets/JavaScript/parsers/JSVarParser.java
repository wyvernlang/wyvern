package wyvern.targets.JavaScript.parsers;

import wyvern.targets.JavaScript.typedAST.JSFunction;
import wyvern.targets.JavaScript.typedAST.JSVar;
import wyvern.tools.parsing.LineParser;
import wyvern.tools.parsing.ParseUtils;
import wyvern.tools.rawAST.ExpressionSequence;
import wyvern.tools.rawAST.Symbol;
import wyvern.tools.typedAST.core.Application;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.Str;
import wyvern.tools.util.Pair;

import static wyvern.tools.types.TypeUtils.arrow;


public class JSVarParser implements LineParser {

	@Override
	public TypedAST parse(TypedAST first,
						  Pair<ExpressionSequence, Environment> ctx) {
		Symbol firstEl = ParseUtils.parseSymbol(ctx);

		return new JSVar(firstEl.name, firstEl.getLocation());
	}
}
