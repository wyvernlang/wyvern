package wyvern.DSL.html.parsing;

import java.util.HashMap;

import wyvern.DSL.html.typedAST.AttrAST;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.ToolError;
import wyvern.tools.parsing.LineParser;
import wyvern.tools.parsing.ParseUtils;
import wyvern.tools.rawAST.ExpressionSequence;
import wyvern.tools.rawAST.LineSequence;
import wyvern.tools.rawAST.RawAST;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.types.Environment;
import wyvern.tools.util.CompilationContext;
import wyvern.tools.util.Pair;

public class AttributeParser implements LineParser {

	@Override
	public TypedAST parse(TypedAST first,
						  CompilationContext ctx) {
		HashMap<String,TypedAST> vars = new HashMap<String,TypedAST>();
		if (ctx.first != null) {
			if (ctx.first.getFirst() instanceof LineSequence) { // All args on individual lines.
				LineSequence lines = ParseUtils.extractLines(ctx);
				for (RawAST line : lines) {
					CompilationContext ctxl = new CompilationContext((ExpressionSequence)line,ctx.second);
					String name = ParseUtils.parseSymbol(ctxl).name;
					ParseUtils.parseSymbol("=", ctxl);
					TypedAST value = ParseUtils.parseExpr(ctxl);
					vars.put(name, value);
				}
			} else {
				ToolError.reportError(ErrorMessage.UNEXPECTED_INPUT, ctx.first);
			}
		}
		
		return new AttrAST(vars, first.getLocation());
	}

}
