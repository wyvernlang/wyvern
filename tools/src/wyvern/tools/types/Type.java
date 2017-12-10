package wyvern.tools.types;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;

import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.HasLocation;
import wyvern.tools.errors.WyvernException;
import wyvern.tools.util.TreeWritable;

public interface Type extends TreeWritable, HasLocation {
	public boolean subtype(Type other, HashSet<SubtypeRelation> subtypes);
	public boolean subtype(Type other);

	/**
	 * @return whether this type is simple or compound.  Used in toString().
	 */
	public boolean isSimple();

	/**
	 * Gets the children of a composite node
	 * @return The children of the node
	 */
	@Deprecated
	Map<String, Type> getChildren();
	/**
	 * Clones the current AST node with the given set of children
	 * @param newChildren The children to create
	 * @return The deep-copied Type node
	 */
    @Deprecated
	Type cloneWithChildren(Map<String, Type> newChildren);

	@Deprecated
	default wyvern.target.corewyvernIL.type.ValueType generateILType() {
        throw new WyvernException("Cannot generate IL form for unresolved type", FileLocation.UNKNOWN);
    }


	public ValueType getILType(GenContext ctx);
}