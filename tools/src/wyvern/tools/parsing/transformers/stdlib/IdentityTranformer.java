package wyvern.tools.parsing.transformers.stdlib;

import wyvern.tools.parsing.transformers.TransformerBase;
import wyvern.tools.typedAST.interfaces.TypedAST;

/**
 * Created by Ben Chung on 8/12/13.
 */
public class IdentityTranformer extends TransformerBase<TypedAST> {
	@Override
	protected TypedAST doTransform(TypedAST transform) {
		return transform;
	}
}
