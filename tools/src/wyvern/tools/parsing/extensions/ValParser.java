package wyvern.tools.parsing.extensions;

import static wyvern.tools.parsing.ParseUtils.parseSymbol;
import wyvern.tools.parsing.LineParser;
import wyvern.tools.parsing.ParseUtils;
import wyvern.tools.rawAST.ExpressionSequence;
import wyvern.tools.typedAST.TypedAST;
import wyvern.tools.typedAST.extensions.ValDeclaration;
import wyvern.tools.types.Environment;
import wyvern.tools.util.Pair;

public class ValParser implements LineParser {
	private ValParser() { }
	private static ValParser instance = new ValParser();
	public static ValParser getInstance() { return instance; }
	

	@Override
	public TypedAST parse(TypedAST first, Pair<ExpressionSequence,Environment> ctx) {
		String varName = ParseUtils.parseSymbol(ctx).name;
		parseSymbol("=", ctx);
		TypedAST exp = ParseUtils.parseExpr(ctx);
		
		return new ValDeclaration(varName, exp);		
	}
}
