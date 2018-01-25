package wyvern.tools.typedAST.core.binding;

import wyvern.tools.types.Type;
import wyvern.tools.util.TreeWritable;

public interface Binding extends TreeWritable {
    String getName();
    Type getType();
}