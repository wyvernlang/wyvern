package wyvern.tools.parsing.transformers;

import wyvern.tools.typedAST.extensions.DSLLit;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.transformers.TypedAST.AbstractASTTransformer;

/**
 * Created by Ben Chung on 3/24/14.
 */
public class DSLTransformer extends AbstractASTTransformer {
	@Override
	public TypedAST transform(TypedAST input) {
		if (input instanceof DSLLit) {
			return defaultTransformation(((DSLLit) input).getAST());
		}
		return defaultTransformation(input);
	}
}
