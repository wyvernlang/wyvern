package wyvern.target.corewyvernIL.support;

import wyvern.target.corewyvernIL.expression.Path;
import wyvern.target.corewyvernIL.type.NominalType;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.ToolError;

public abstract class TypeContext {
	public TypeContext extend(String var, ValueType type) {
		return new VarBindingContext(var, type, this);
	}
	
	public boolean isAssumedSubtype(NominalType t1, NominalType t2) {
		if (getNext() == null)
			return false;
		else
			return getNext().isAssumedSubtype(t1, t2);
	}
	
	/*
	 * Returns the type of this variable in the context
	 */
	public abstract ValueType lookupTypeOf(String varName);
		
	public boolean isPresent(String varName, boolean isValue) {
		if (getNext() == null)
			return false;
		else
			return getNext().isPresent(varName, isValue);
	}
	
	public static TypeContext empty() {
		return theEmpty;
	}
	
	private static TypeContext theEmpty = new EmptyTypeContext(); 
	
	protected abstract TypeContext getNext();
	protected abstract String endToString();
}
