package wyvern.tools.parsing.extensions;

import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.ToolError;
import wyvern.tools.parsing.CoreParser;
import wyvern.tools.parsing.LineParser;
import wyvern.tools.parsing.ParseUtils;
import wyvern.tools.rawAST.ExpressionSequence;
import wyvern.tools.rawAST.LineSequence;
import wyvern.tools.rawAST.Symbol;
import wyvern.tools.typedAST.Declaration;
import wyvern.tools.typedAST.TypedAST;
import wyvern.tools.typedAST.extensions.declarations.ClassDeclaration;
import wyvern.tools.types.Environment;
import wyvern.tools.types.extensions.Unit;
import wyvern.tools.util.Pair;

/**
 * 	class NAME
 * 		[implements NAME]
 *		[class implements NAME]
 * 		DELCARATION*
 */

public class ClassParser implements LineParser {
	private ClassParser() { }
	private static ClassParser instance = new ClassParser();
	public static ClassParser getInstance() { return instance; }

	@Override
	public TypedAST parse(TypedAST first, Pair<ExpressionSequence,Environment> ctx) {
		if (ParseUtils.checkFirst("meth", ctx)) { // Parses "class meth".
			ParseUtils.parseSymbol(ctx);
			return MethParser.getInstance().parse(first, ctx, Unit.getInstance());
		}
		
		Symbol s = ParseUtils.parseSymbol(ctx);
		String clsName = s.name;
		int clsNameLine = s.getLine();

		TypedAST declAST = null;
		String implementsName = "";
		String implementsClassName = "";

		if (ctx.first == null) {
			// Empty body in the class declaration is OK.
		} else {
			LineSequence lines = ParseUtils.extractLines(ctx); // Get potential body.
			
			if (lines.getFirst() != null && lines.getFirst().getFirst() != null &&
					lines.getFirst().getFirst().toString().equals("implements")) { // FIXME: hack, detected implements
				implementsName = lines.getFirst().getRest().getFirst().toString();
				lines.children.remove(0);
			}

			if (lines.getFirst() != null && lines.getFirst().getFirst() != null &&
					lines.getFirst().getFirst().toString().equals("class")) { // FIXME: hack, detected class
				if (lines.getFirst().getRest() != null && lines.getFirst().getRest().getFirst() != null &&
						lines.getFirst().getRest().getFirst().toString().equals("implements")) { // FIXME: hack, detected implements
					implementsClassName = lines.getFirst().getRest().getRest().getFirst().toString();
					lines.children.remove(0);
				}
			}

			// Process body.
			if (ctx.first != null)
				ToolError.reportError(ErrorMessage.UNEXPECTED_INPUT, ctx.first);
		
			declAST = lines.accept(CoreParser.getInstance(), ctx.second);
			if (!(declAST instanceof Declaration))
				ToolError.reportError(ErrorMessage.UNEXPECTED_INPUT, ctx.first);
		}

		return new ClassDeclaration(clsName, implementsName, implementsClassName, (Declaration) declAST, clsNameLine);
	}
}
