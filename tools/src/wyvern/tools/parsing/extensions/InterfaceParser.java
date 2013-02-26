package wyvern.tools.parsing.extensions;

import wyvern.tools.parsing.CoreParser;
import wyvern.tools.parsing.LineParser;
import wyvern.tools.parsing.ParseUtils;
import wyvern.tools.rawAST.ExpressionSequence;
import wyvern.tools.rawAST.LineSequence;
import wyvern.tools.typedAST.Declaration;
import wyvern.tools.typedAST.TypedAST;
import wyvern.tools.typedAST.extensions.InterfaceDeclaration;
import wyvern.tools.types.Environment;
import wyvern.tools.util.Pair;

public class InterfaceParser implements LineParser {
	private InterfaceParser() { }
	private static InterfaceParser instance = new InterfaceParser();
	public static InterfaceParser getInstance() { return instance; }

	@Override
	public TypedAST parse(TypedAST first, Pair<ExpressionSequence,Environment> ctx) {
		String clsName = ParseUtils.parseSymbol(ctx).name;

		TypedAST declAST = null;
		if (ctx.first == null) {
			// Empty body in the interface declaration is OK.
		} else {
			// Process body.
			LineSequence lines = ParseUtils.extractLines(ctx);
			if (ctx.first != null)
				throw new RuntimeException("parse error");
		
			declAST = lines.accept(CoreParser.getInstance(), ctx.second);
			if (!(declAST instanceof Declaration))
				throw new RuntimeException("parse error");
		}

		return new InterfaceDeclaration(clsName, (Declaration) declAST);
	}
}
