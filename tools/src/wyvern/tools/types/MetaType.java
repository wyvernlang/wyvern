package wyvern.tools.types;

import wyvern.tools.typedAST.interfaces.Value;

public interface MetaType extends Type {
    Value getMetaObj();
}
