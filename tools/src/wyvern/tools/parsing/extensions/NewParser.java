package wyvern.tools.parsing.extensions;

import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.ToolError;
import wyvern.tools.parsing.LineParser;
import wyvern.tools.parsing.ParseUtils;
import wyvern.tools.rawAST.ExpressionSequence;
import wyvern.tools.rawAST.Line;
import wyvern.tools.rawAST.LineSequence;
import wyvern.tools.rawAST.Parenthesis;
import wyvern.tools.typedAST.TypedAST;
import wyvern.tools.typedAST.extensions.New;
import wyvern.tools.typedAST.extensions.Variable;
import wyvern.tools.typedAST.extensions.declarations.ClassDeclaration;
import wyvern.tools.types.Environment;
import wyvern.tools.util.Pair;

/**
 *	new NAME
 *		list = Link(firstElement, null)
 */

public class NewParser implements LineParser {
	private NewParser() { }
	private static NewParser instance = new NewParser();
	public static NewParser getInstance() { return instance; }

	@Override
	public TypedAST parse(TypedAST first, Pair<ExpressionSequence,Environment> ctx) {
		Variable classExpr = ParseUtils.parseVariable(ctx); // Alex does not think this should be a Variable but not sure...

		TypedAST args = null;
		if (ctx.first != null) {
			if (ctx.first.getFirst() instanceof Parenthesis) { // All args as parameters.
				// args = ParseUtils.parseExprList(ctx);
				
				Parenthesis paren = ParseUtils.extractParen(ctx);
				
				// TODO: Parse this properly.
				// System.out.println(paren);
			} else if (ctx.first.getFirst() instanceof LineSequence) { // All args on individual lines.
				LineSequence lines = ParseUtils.extractLines(ctx);

				// TODO: Parse this properly.
				// System.out.println(lines);
			} else {
				ToolError.reportError(ErrorMessage.UNEXPECTED_INPUT, ctx.first);
			}
		}
		
		return new New(classExpr, args, classExpr.getLine());
	}
}
