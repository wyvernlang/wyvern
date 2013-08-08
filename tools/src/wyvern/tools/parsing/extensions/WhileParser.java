package wyvern.tools.parsing.extensions;

import wyvern.tools.parsing.BodyParser;
import wyvern.tools.parsing.LineParser;
import wyvern.tools.parsing.ParseUtils;
import wyvern.tools.rawAST.ExpressionSequence;
import wyvern.tools.typedAST.core.expressions.WhileStatement;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.types.Environment;
import wyvern.tools.util.CompilationContext;
import wyvern.tools.util.Pair;

public class WhileParser implements LineParser {
	private WhileParser() { }
	private static WhileParser instance = new WhileParser();
	public static WhileParser getInstance() { return instance; }

	@Override
	public TypedAST parse(TypedAST first,
						  CompilationContext ctx) {
		TypedAST conditional = ParseUtils.parseCond(ctx);
		TypedAST body = ParseUtils.extractLines(ctx).accept(BodyParser.getInstance(), ctx.second);
		
		return new WhileStatement(conditional,body,first.getLocation());
	}

}
