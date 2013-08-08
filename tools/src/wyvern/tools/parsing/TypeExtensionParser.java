package wyvern.tools.parsing;

import wyvern.tools.rawAST.ExpressionSequence;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.types.Environment;
import wyvern.tools.util.CompilationContext;
import wyvern.tools.util.Pair;

public interface TypeExtensionParser {
	Pair<Environment, RecordTypeParser> parseRecord(TypedAST first,
													CompilationContext ctx);
	boolean typeRequiredPartialParse(CompilationContext seq);
}
