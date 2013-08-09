package wyvern.tools.parsing.extensions;

import wyvern.tools.parsing.BodyParser;
import wyvern.tools.parsing.LineParser;
import wyvern.tools.parsing.ParseUtils;
import wyvern.tools.typedAST.core.expressions.WhileStatement;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.util.CompilationContext;

public class WhileParser implements LineParser {
	private WhileParser() { }
	private static WhileParser instance = new WhileParser();
	public static WhileParser getInstance() { return instance; }

	@Override
	public TypedAST parse(TypedAST first,
						  CompilationContext ctx) {
		TypedAST conditional = ParseUtils.parseCond(ctx);
		TypedAST body = ParseUtils.extractLines(ctx).accept(BodyParser.getInstance(), ctx.getEnv());
		
		return new WhileStatement(conditional,body,first.getLocation());
	}

}
