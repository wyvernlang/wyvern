package wyvern.target.corewyvernIL.type;

import wyvern.target.corewyvernIL.EmitOIR;
import wyvern.target.corewyvernIL.support.TypeContext;

public abstract class ValueType extends CaseType implements EmitOIR {

	/**
	 * Returns the equivalent structural type, or null if the structural type is unknown
	 */
	public StructuralType getStructuralType() {
		return null;
	}

	public boolean isSubtypeOf(Type t, TypeContext ctx) {
		return equals(t); // default
	}
}
