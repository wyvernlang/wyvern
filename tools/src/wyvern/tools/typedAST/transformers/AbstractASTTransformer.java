package wyvern.tools.typedAST.transformers;

import wyvern.tools.typedAST.interfaces.TypedAST;

import java.util.Dictionary;
import java.util.Enumeration;
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
