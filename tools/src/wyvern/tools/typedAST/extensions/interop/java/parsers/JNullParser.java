package wyvern.tools.typedAST.extensions.interop.java.parsers;

import wyvern.tools.parsing.LineParser;
import wyvern.tools.rawAST.ExpressionSequence;
import wyvern.tools.typedAST.extensions.interop.java.typedAST.JNull;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.types.Environment;
import wyvern.tools.util.Pair;

public class JNullParser implements LineParser {
	@Override
	public TypedAST parse(TypedAST first, Pair<ExpressionSequence, Environment> ctx) {
		return new JNull();
	}
}
