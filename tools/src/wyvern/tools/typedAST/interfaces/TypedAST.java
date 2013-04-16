package wyvern.tools.typedAST.interfaces;

import wyvern.tools.errors.HasLocation;
import wyvern.tools.parsing.LineParser;
import wyvern.tools.parsing.LineSequenceParser;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.util.TreeWritable;

public interface TypedAST extends TreeWritable, HasLocation {

	/** should call typecheck() before getType() -- except maybe for declarations */
	Type getType();
	Type typecheck(Environment env);
	
	/** an interpreter */
	Value evaluate(Environment env);
	
	/** may return null */
	LineParser getLineParser();

	/** may return null */
	LineSequenceParser getLineSequenceParser();

}
