package wyvern.tools.typedAST.transformers.TypedAST;

import wyvern.tools.typedAST.interfaces.TypedAST;

import java.util.Map;

public abstract class AbstractASTTransformer implements AstTransformer {
	protected final TypedAST defaultTransformation(TypedAST input) {
		Map<String, TypedAST> children = input.getChildren();
		for (String key : children.keySet()) {
			children.put(key, this.transform(children.get(key)));
		}
		return input.cloneWithChildren(children);
	}
}
