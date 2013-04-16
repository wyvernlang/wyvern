package wyvern.tools.typedAST.binding;

import wyvern.tools.typedAST.Value;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.util.TreeWritable;

public interface Binding extends TreeWritable {
	String getName();
	Type getType();
}