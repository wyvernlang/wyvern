package wyvern.tools.parsing;

import wyvern.tools.rawAST.ExpressionSequence;
import wyvern.tools.typedAST.TypedAST;
import wyvern.tools.types.Environment;
import wyvern.tools.util.Pair;

public interface DeclParser extends LineParser {
	public interface ContParser {
		TypedAST parse(Environment env);
	}
	Pair<Environment, ContParser> parseDeferred(TypedAST first, Pair<ExpressionSequence,Environment> ctx);
	Environment getBodyEnv(Environment decls);
}
