package wyvern.tools.parsing.extensions;

import java.util.HashMap;

import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.ToolError;
import wyvern.tools.parsing.BodyParser;
import wyvern.tools.parsing.LineParser;
import wyvern.tools.parsing.ParseUtils;
import wyvern.tools.rawAST.ExpressionSequence;
import wyvern.tools.rawAST.Line;
import wyvern.tools.rawAST.LineSequence;
import wyvern.tools.rawAST.Parenthesis;
import wyvern.tools.rawAST.RawAST;
import wyvern.tools.rawAST.Symbol;
import wyvern.tools.typedAST.Assignment;
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
		HashMap<String,TypedAST> vars = new HashMap<String,TypedAST>();
		if (ctx.first != null) {
			if (ctx.first.getFirst() instanceof LineSequence) { // All args on individual lines.
				LineSequence lines = ParseUtils.extractLines(ctx);
				for (RawAST line : lines) {
					Pair<ExpressionSequence, Environment> ctxl = new Pair<ExpressionSequence,Environment>((ExpressionSequence)line,ctx.second);
					String name = ParseUtils.parseSymbol(ctxl).name;
					ParseUtils.parseSymbol("=", ctxl);
					TypedAST value = ParseUtils.parseExpr(ctxl);
					vars.put(name, value);
				}
				// TODO: Parse this properly.
				// System.out.println(lines);
			} else {
				ToolError.reportError(ErrorMessage.UNEXPECTED_INPUT, ctx.first);
			}
		}
		
		return new New(vars, first.getLocation());
	}
}
