package wyvern.tools.typedAST.transformers.Types;

import wyvern.tools.types.Type;

import java.util.Map;

public abstract class AbstractTypeTransformer implements TypeTransformer {
	protected final Type defaultTransformation(Type input) {
		Map<String, Type> children = input.getChildren();
		for (String key : children.keySet()) {
			children.put(key, this.transform(children.get(key)));
		}
		return input.cloneWithChildren(children);
	}
}
