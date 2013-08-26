package wyvern.targets.Java.types;

import wyvern.tools.parsing.LineParser;
import wyvern.tools.types.SubtypeRelation;
import wyvern.tools.types.Type;
import wyvern.tools.util.TreeWriter;

import java.util.HashSet;

/**
 * Created with IntelliJ IDEA.
 * User: Ben Chung
 * Date: 8/26/13
 * Time: 9:19 AM
 * To change this template use File | Settings | File Templates.
 */
public class RefType implements Type {
	private Type inner;

	public RefType(Type inner) {
		this.inner = inner;
	}

	public Type getInner() {
		return inner;
	}

	@Override
	public boolean subtype(Type other, HashSet<SubtypeRelation> subtypes) {
		return false;
	}

	@Override
	public boolean subtype(Type other) {
		return false;
	}

	@Override
	public LineParser getParser() {
		return null;
	}

	@Override
	public boolean isSimple() {
		return false;
	}

	@Override
	public void writeArgsToTree(TreeWriter writer) {
	}
}
