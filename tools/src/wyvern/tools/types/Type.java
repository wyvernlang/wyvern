package wyvern.tools.types;

import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.typedAST.core.binding.typechecking.TypeBinding;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.util.TreeWritable;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;

public interface Type extends TreeWritable {
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
	Map<String, Type> getChildren();
	/**
	 * Clones the current AST node with the given set of children
	 * @param newChildren The children to create
	 * @return The deep-copied Type node
	 */
	Type cloneWithChildren(Map<String, Type> newChildren);

	Optional<TypeBinding> getResolvedBinding();
	void setResolvedBinding(TypeBinding binding);
	Type cloneWithBinding(TypeBinding binding);

	@Deprecated
	wyvern.target.corewyvernIL.type.ValueType generateILType();
	public ValueType getILType(GenContext ctx);
}