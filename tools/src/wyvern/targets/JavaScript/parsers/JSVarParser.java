package wyvern.targets.JavaScript.parsers;

import wyvern.targets.JavaScript.typedAST.JSVar;
import wyvern.tools.parsing.LineParser;
import wyvern.tools.parsing.ParseUtils;
import wyvern.tools.rawAST.ExpressionSequence;
import wyvern.tools.rawAST.Symbol;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.types.Environment;
import wyvern.tools.util.CompilationContext;
import wyvern.tools.util.Pair;


public class JSVarParser implements LineParser {

	@Override
	public TypedAST parse(TypedAST first,
						  CompilationContext ctx) {
		Symbol firstEl = ParseUtils.parseSymbol(ctx);

		return new JSVar(firstEl.name, firstEl.getLocation());
	}
}
