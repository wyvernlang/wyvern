package wyvern.target.corewyvernIL.support;

import wyvern.target.corewyvernIL.expression.Path;
import wyvern.target.corewyvernIL.type.NominalType;
import wyvern.target.corewyvernIL.type.ValueType;

public abstract class TypeContext {
	public TypeContext extend(String var, ValueType type) {
		return new VarBindingContext(var, type, this);
	}
	
	public boolean isAssumedSubtype(NominalType t1, ValueType t2) {
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
	
	public String desugarType(Path var, String member) {
        if (getNext() == null)
            return null;
        else
            return getNext().desugarType(var, member);
	}
	
	private static TypeContext theEmpty = new EmptyTypeContext(); 
	
	protected abstract TypeContext getNext();
	protected abstract String endToString();
}
