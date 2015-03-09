package wyvern.tools.parsing.transformers;

import wyvern.tools.parsing.DSLLit;
import wyvern.tools.typedAST.core.expressions.KeywordInvocation;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.transformers.TypedAST.AbstractASTTransformer;

public class DSLTransformer extends AbstractASTTransformer {
	@Override
	public TypedAST transform(TypedAST input) {
		if (input instanceof DSLLit) {
			return new DSLTransformer().transform(defaultTransformation(((DSLLit) input).getAST()));
		}
		if (input instanceof KeywordInvocation) {
			//TODO
		}
		return defaultTransformation(input);
	}
}
