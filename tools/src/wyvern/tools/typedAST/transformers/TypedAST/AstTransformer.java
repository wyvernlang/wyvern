package wyvern.tools.typedAST.transformers.TypedAST;

import wyvern.tools.typedAST.interfaces.TypedAST;

public interface AstTransformer {
	public TypedAST transform(TypedAST input);
}
