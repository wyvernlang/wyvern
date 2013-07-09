package wyvern.tools.typedAST.extensions.interop.java.types;

import wyvern.tools.parsing.LineParser;
import wyvern.tools.types.SubtypeRelation;
import wyvern.tools.types.Type;
import wyvern.tools.util.TreeWriter;

import java.util.HashSet;

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
	public void writeArgsToTree(TreeWriter writer) {
	}
}
