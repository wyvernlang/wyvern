package wyvern.tools.parsing.extensions;

import wyvern.tools.parsing.LineParser;
import wyvern.tools.parsing.ParseUtils;
import wyvern.tools.rawAST.ExpressionSequence;
import wyvern.tools.typedAST.TypedAST;
import wyvern.tools.typedAST.extensions.TypeDeclaration;
import wyvern.tools.types.Environment;
import wyvern.tools.util.Pair;

public class TypeDeclarationParser implements LineParser {
	private TypeDeclarationParser() { }
	private static TypeDeclarationParser instance = new TypeDeclarationParser();
	public static TypeDeclarationParser getInstance() { return instance; }
	

	@Override
	public TypedAST parse(TypedAST first, Pair<ExpressionSequence,Environment> ctx) {
		String typeName = ParseUtils.parseSymbol(ctx).name;
		
		return new TypeDeclaration(typeName, ctx.second);		
	}
}
