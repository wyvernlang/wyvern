package wyvern.tools.typedAST.transformers.Types;

import wyvern.tools.types.Type;

public interface TypeTransformer {
    Type transform(Type type);
}
