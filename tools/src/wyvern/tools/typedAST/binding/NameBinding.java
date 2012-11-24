package wyvern.tools.typedAST.binding;

import wyvern.tools.typedAST.TypedAST;

public interface NameBinding extends Binding {
	TypedAST getUse();
}
