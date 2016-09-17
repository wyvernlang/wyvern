package wyvern.target.corewyvernIL.type;

import wyvern.target.corewyvernIL.IASTNode;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.expression.Value;
import wyvern.target.corewyvernIL.support.EvalContext;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.support.View;

public abstract class ValueType extends CaseType implements IASTNode {
	/**
	 * Returns the equivalent structural type.  If the structural type
	 * is unknown (e.g. because this is a nominal type and the
	 * corresponding type member is abstract from this context) then
	 * the empty structural type is returned.
	 *  
	 * @param ctx TODO
	 */
	public final StructuralType getStructuralType(TypeContext ctx) {
		return getStructuralType(ctx, StructuralType.getEmptyType());
	}

	/**
	 * Returns the equivalent structural type.  If the structural type
	 * is unknown (e.g. because this is a nominal type and the
	 * corresponding type member is abstract from this context) then
	 * the default type is returned 
	 *  
	 * @param ctx TODO
	 */
	public StructuralType getStructuralType(TypeContext ctx, StructuralType theDefault) {
		return theDefault;
	}
	
	/** For nominal types that are transitively equivalent to a known type, return that type.
	 *  For all other types, this is the identity.
	 */
	public ValueType getCanonicalType(TypeContext ctx) {
		return this;
	}

	public boolean isResource(TypeContext ctx) {
		return false;
	}

	public boolean isSubtypeOf(ValueType t, TypeContext ctx) {
		return t instanceof DynamicType || equals(t); // default
	}

	/** Find the declaration type with the specified name, or return null if it is not present */
	public DeclType findDecl(String declName, TypeContext ctx) {
		StructuralType st = getStructuralType(ctx);
		if (st == null)
			return null;
		return st.findDecl(declName, ctx);
	}

	/**
	 * Returns a type that is equivalent to this type
	 * under the View v.  If v maps x to y.f, for example,
	 * then a type of the form x.g.T will be mapped to the
	 * type y.f.g.T
	 */
	public abstract ValueType adapt(View v);

	public boolean equalsInContext(ValueType otherType, TypeContext ctx) {
		return this.isSubtypeOf(otherType, ctx) && otherType.isSubtypeOf(this, ctx);
	}

	/**
	 * Evaluates any metadata that might be present in this type to a value  
	 */
	public ValueType interpret(EvalContext ctx) {
		return this;
	}
	
	/**
	 * Gets the metadata, if any, for this type.
	 * Returns null if no metadata is associated with this type.
	 */
	public Value getMetadata(TypeContext ctx) {
		return null;
	}
	
	/**
	 * Checks if this type is well-formed, throwing an exception if not
	 */
	abstract public void checkWellFormed(TypeContext ctx);
	
	/**
	 * Returns this type, avoiding the named variable if possible
	 * @param count TODO
	 */
	public final ValueType avoid(String varName, TypeContext ctx) {
		return doAvoid(varName, ctx, 0);
	}
	// TODO: depth limit is hacky, find a more principled approach to avoidance
	abstract public ValueType doAvoid(String varName, TypeContext ctx, int depth);
	public static final int MAX_RECURSION_DEPTH = 10; 
}
