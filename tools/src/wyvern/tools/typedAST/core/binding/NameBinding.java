package wyvern.tools.typedAST.core.binding;

import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;

public interface NameBinding extends Binding {
    @Deprecated
	TypedAST getUse();
}