package wyvern.target.corewyvernIL.type;

import wyvern.target.corewyvernIL.EmitOIR;
import wyvern.target.corewyvernIL.decl.Declaration;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.support.View;

public abstract class ValueType extends CaseType implements EmitOIR {

	/**
	 * Returns the equivalent structural type.  If the structural type
	 * is unknown (e.g. because this is a nominal type and the
	 * corresponding type member is abstract from this context) then
	 * the empty structural type is returned.
	 *  
	 * @param ctx TODO
	 */
	public StructuralType getStructuralType(TypeContext ctx) {
		return StructuralType.getEmptyType();
	}

	public boolean isSubtypeOf(Type t, TypeContext ctx) {
		return equals(t); // default
	}

	/** Find the declaration type with the specified name, or return null if it is not present */
	public DeclType findDecl(String declName, TypeContext ctx) {
		StructuralType st = getStructuralType(ctx);
		if (st == null)
			return null;
		return st.findDecl(declName, ctx);
	}

	public abstract ValueType adapt(View v);

	public boolean equalsInContext(ValueType otherType, TypeContext ctx) {
		return this.isSubtypeOf(otherType, ctx) && otherType.isSubtypeOf(this, ctx);
	}
}
