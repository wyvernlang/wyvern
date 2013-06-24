package wyvern.tools.typedAST.extensions.interop.java.parsers;

import wyvern.tools.parsing.LineParser;
import wyvern.tools.parsing.ParseUtils;
import wyvern.tools.rawAST.ExpressionSequence;
import wyvern.tools.typedAST.extensions.interop.java.Util;
import wyvern.tools.typedAST.extensions.interop.java.typedAST.JavaType;
import wyvern.tools.typedAST.extensions.interop.java.types.JavaClassType;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.util.Pair;

public class JImportParser implements LineParser {
	@Override
	public TypedAST parse(TypedAST first, Pair<ExpressionSequence, Environment> ctx) {
		StringBuilder canonicalClassName = new StringBuilder();
		while (!ParseUtils.checkFirst("as", ctx)) {
			canonicalClassName.append(ParseUtils.parseSymbol(ctx).name);
		}
		ParseUtils.parseSymbol("as",ctx);

		String typeName = ParseUtils.parseSymbol(ctx).name;

		Class loaded = null;
		try {
			loaded = JImportParser.class.getClassLoader().loadClass(canonicalClassName.toString());
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Invalid class", e);
		}

		Type bound = Util.javaToWyvType(loaded);
		if (bound instanceof JavaClassType)
			return ((JavaClassType) bound).getDecl();
		return new JavaType(typeName, bound);
	}
}
