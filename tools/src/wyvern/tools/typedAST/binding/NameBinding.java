package wyvern.tools.typedAST.binding;

import wyvern.tools.typedAST.TypedAST;
import wyvern.tools.typedAST.Value;
import wyvern.tools.types.Environment;

public interface NameBinding extends Binding {
	TypedAST getUse();
	Value getValue(Environment env);
}