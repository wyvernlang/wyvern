package wyvern.tools.typedAST.extensions.interop.java.types;

import wyvern.tools.parsing.LineParser;
import wyvern.tools.types.SubtypeRelation;
import wyvern.tools.types.Type;
import wyvern.tools.util.TreeWriter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class JNullType implements Type {
	@Override
	public boolean subtype(Type other, HashSet<SubtypeRelation> subtypes) {
		return other instanceof JavaClassType;
	}

	@Override
	public boolean subtype(Type other) {
		return other instanceof JavaClassType;
	}

	@Override
	public LineParser getParser() {
		return null;
	}

	@Override
	public boolean isSimple() {
		return true;
	}

	@Override
	public Map<String, Type> getChildren() {
		return new HashMap<>();
	}

	@Override
	public Type cloneWithChildren(Map<String, Type> newChildren) {
		return this;
	}

	@Override
	public void writeArgsToTree(TreeWriter writer) {
	}
}
