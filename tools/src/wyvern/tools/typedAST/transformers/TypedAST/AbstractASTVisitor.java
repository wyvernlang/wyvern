package wyvern.tools.typedAST.transformers.TypedAST;

import wyvern.tools.typedAST.interfaces.TypedAST;

import java.util.Map;

public abstract class AbstractASTVisitor implements AstTransformer {
	protected final TypedAST defaultTransformation(TypedAST input) {
		Map<String, TypedAST> children = input.getChildren();
		if (children == null)
			return input;
		for (String key : children.keySet()) {
			this.transform(children.get(key));
		}
		return input;
	}
}