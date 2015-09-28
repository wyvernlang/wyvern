package wyvern.target.corewyvernIL.type;

import wyvern.target.corewyvernIL.EmitOIR;
import wyvern.target.corewyvernIL.decl.Declaration;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.support.View;

public abstract class ValueType extends CaseType implements EmitOIR {

	/**
	 * Returns the equivalent structural type, or null if the structural type is unknown
	 * @param ctx TODO
	 */
	public StructuralType getStructuralType(TypeContext ctx) {
		return null;
	}

	public boolean isSubtypeOf(Type t, TypeContext ctx) {
		return equals(t); // default
	}

	/** Find the declaration type with the specified name, or return null if it is not present */
	public DeclType findDecl(String declName) {
		return null;
	}

	public abstract ValueType adapt(View v);

	public boolean equalsInContext(ValueType otherType, TypeContext ctx) {
		return this.isSubtypeOf(otherType, ctx) && otherType.isSubtypeOf(this, ctx);
	}
}
