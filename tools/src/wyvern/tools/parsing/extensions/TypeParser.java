package wyvern.tools.parsing.extensions;

import wyvern.tools.parsing.LineParser;
import wyvern.tools.parsing.ParseUtils;
import wyvern.tools.rawAST.ExpressionSequence;
import wyvern.tools.typedAST.TypedAST;
import wyvern.tools.typedAST.extensions.TypeDeclaration;
import wyvern.tools.types.Environment;
import wyvern.tools.util.Pair;

public class TypeParser implements LineParser {
	private TypeParser() { }
	private static TypeParser instance = new TypeParser();
	public static TypeParser getInstance() { return instance; }
	

	@Override
	public TypedAST parse(TypedAST first, Pair<ExpressionSequence,Environment> ctx) {
		String typeName = ParseUtils.parseSymbol(ctx).name;
		
		return new TypeDeclaration(typeName, ctx.second);		
	}
}
