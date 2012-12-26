package wyvern.tools.typedAST;

import wyvern.tools.parsing.LineParser;
import wyvern.tools.parsing.LineSequenceParser;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.util.TreeWriter;

public interface CoreAST extends TypedAST {
	void accept(CoreASTVisitor visitor);
}
