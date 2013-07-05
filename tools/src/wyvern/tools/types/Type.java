package wyvern.tools.types;

import wyvern.tools.parsing.LineParser;
import wyvern.tools.util.TreeWritable;
import java.util.HashSet;

public interface Type extends TreeWritable {
	public boolean subtype(Type other, HashSet<SubtypeRelation> subtypes);
	public boolean subtype(Type other);


    /*
     * Get the parser associated with this type
     * @return The type-associated parser
     */
    public LineParser getParser();

	/**
	 * @return whether this type is simple or compound.  Used in toString().
	 */
	public boolean isSimple();
}