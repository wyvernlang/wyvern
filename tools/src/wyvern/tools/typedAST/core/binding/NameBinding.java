package wyvern.tools.typedAST.core.binding;

import wyvern.tools.typedAST.interfaces.TypedAST;

public interface NameBinding extends Binding {
    @Deprecated
    TypedAST getUse();
}