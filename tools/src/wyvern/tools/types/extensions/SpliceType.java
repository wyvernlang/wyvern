package wyvern.tools.types.extensions;

import wyvern.tools.types.SubtypeRelation;
import wyvern.tools.types.Type;
import wyvern.tools.util.TreeWriter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class SpliceType implements Type {
	private final Type inner;

	public SpliceType(Type inner) {
		this.inner = inner;
	}

	@Override
	public boolean subtype(Type other, HashSet<SubtypeRelation> subtypes) {
		throw new RuntimeException();
	}

	@Override
	public boolean subtype(Type other) {
		throw new RuntimeException();
	}

	@Override
	public boolean isSimple() {
		throw new RuntimeException();
	}

	@Override
	public Map<String, Type> getChildren() {
		HashMap<String,Type> children = new HashMap<>();
		children.put("inner", inner);
		return children;
	}

	@Override
	public Type cloneWithChildren(Map<String, Type> newChildren) {
		return new SpliceType(newChildren.get("inner"));
	}

	@Override
	public void writeArgsToTree(TreeWriter writer) {

	}

	public Type getInner() {
		return inner;
	}
}
