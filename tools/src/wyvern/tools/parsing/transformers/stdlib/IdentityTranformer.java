package wyvern.tools.parsing.transformers.stdlib;

import wyvern.tools.parsing.transformers.TransformerBase;
import wyvern.tools.typedAST.core.Sequence;
import wyvern.tools.typedAST.core.declarations.ImportDeclaration;
import wyvern.tools.typedAST.interfaces.TypedAST;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Ben Chung on 8/12/13.
 */
public class IdentityTranformer extends TransformerBase<TypedAST> {
	@Override
	protected TypedAST doTransform(TypedAST transform) {
		return transform;
	}
}
