package wyvern.tools.typedAST.core.binding;

import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.types.Environment;

public interface NameBinding extends Binding {
	TypedAST getUse();
	Value getValue(Environment env);
}