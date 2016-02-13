package wyvern.tools.typedAST.transformers.Types;

import java.util.Map;

import wyvern.tools.types.Type;

public abstract class AbstractTypeTransformer implements TypeTransformer {
	protected final Type defaultTransformation(Type input) {
		Map<String, Type> children = input.getChildren();
		for (String key : children.keySet()) {
			children.put(key, this.transform(children.get(key)));
		}
		return input.cloneWithChildren(children);
	}
}
