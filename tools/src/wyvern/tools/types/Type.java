package wyvern.tools.types;

import wyvern.tools.util.TreeWritable;

import java.util.HashSet;
import java.util.Map;

public interface Type extends TreeWritable {
	public boolean subtype(Type other, HashSet<SubtypeRelation> subtypes);
	public boolean subtype(Type other);

	/**
	 * @return whether this type is simple or compound.  Used in toString().
	 */
	public boolean isSimple();

}