package wyvern.tools.parsing.extensions;

import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.ToolError;
import wyvern.tools.parsing.BodyParser;
import wyvern.tools.parsing.LineParser;
import wyvern.tools.parsing.ParseUtils;
import wyvern.tools.rawAST.ExpressionSequence;
import wyvern.tools.rawAST.LineSequence;
import wyvern.tools.rawAST.Symbol;
import wyvern.tools.typedAST.Declaration;
import wyvern.tools.typedAST.TypedAST;
import wyvern.tools.typedAST.extensions.DeclSequence;
import wyvern.tools.typedAST.extensions.declarations.TypeDeclaration;
import wyvern.tools.types.Environment;
import wyvern.tools.util.Pair;

public class TypeParser implements LineParser {
	private TypeParser() { }
	private static TypeParser instance = new TypeParser();
	public static TypeParser getInstance() { return instance; }
	

	@Override
	public TypedAST parse(TypedAST first, Pair<ExpressionSequence,Environment> ctx) {
		Symbol s = ParseUtils.parseSymbol(ctx);
		String clsName = s.name;
		FileLocation clsNameLine = s.getLocation();

		TypedAST declAST = null;
		if (ctx.first == null) {
			// Empty body in the interface declaration is OK.
		} else {
			// Process body.
			LineSequence lines = ParseUtils.extractLines(ctx);
			if (ctx.first != null)
				ToolError.reportError(ErrorMessage.UNEXPECTED_INPUT, ctx.first);

			declAST = lines.accept(BodyParser.getInstance(), ctx.second);
		}

		return new TypeDeclaration(clsName, DeclSequence.getDeclSeq(declAST), clsNameLine);
	}
}
