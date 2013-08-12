package wyvern.tools.parsing.transformers;

import wyvern.tools.typedAST.interfaces.TypedAST;

public interface TypedASTTransformer<T extends TypedAST> {
	T transform(TypedAST root);
}
