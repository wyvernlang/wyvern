package wyvern.tools.parsing.transformers;

import wyvern.tools.typedAST.interfaces.TypedAST;

/**
 * Created by Ben Chung on 8/12/13.
 */
public abstract class TransformerBase<T extends TypedAST> implements TypedASTTransformer<T> {
	TypedASTTransformer base = null;
	protected TransformerBase() {
	}
	protected TransformerBase(TypedASTTransformer base) {
		this.base = base;
	}
	@Override
	public T transform(TypedAST root) {
		if (base == null)
			return doTransform(root);
		return doTransform(base.transform(root));
	}

	protected abstract T doTransform(TypedAST transform);
}
