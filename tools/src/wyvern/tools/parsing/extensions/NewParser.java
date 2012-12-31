package wyvern.tools.parsing.extensions;

import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.ToolError;
import wyvern.tools.parsing.LineParser;
import wyvern.tools.parsing.ParseUtils;
import wyvern.tools.rawAST.ExpressionSequence;
import wyvern.tools.typedAST.TypedAST;
import wyvern.tools.typedAST.extensions.ClassDeclaration;
import wyvern.tools.typedAST.extensions.New;
import wyvern.tools.typedAST.extensions.Variable;
import wyvern.tools.types.Environment;
import wyvern.tools.util.Pair;

/**
 * Parses "new x()"
 * 
 * TODO: Could specify as:   "new" symbol ()
 */

public class NewParser implements LineParser {
	private NewParser() { }
	private static NewParser instance = new NewParser();
	public static NewParser getInstance() { return instance; }

	@Override
	public TypedAST parse(TypedAST first, Pair<ExpressionSequence,Environment> ctx) {
		Variable classExpr = ParseUtils.parseVariable(ctx);
		TypedAST args = ParseUtils.parseExprList(ctx);
		
		return new New(classExpr, args);
	}
}
