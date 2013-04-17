package wyvern.tools.types;

import wyvern.tools.util.TreeWritable;
import java.util.HashSet;

public interface Type extends TreeWritable {
	public boolean subtype(Type other, HashSet<SubtypeRelation> subtypes);
	public boolean subtype(Type other);
}