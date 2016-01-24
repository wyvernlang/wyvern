package wyvern.target.corewyvernIL.support;

import wyvern.target.corewyvernIL.type.ValueType;

public abstract class TypeContext {
	public TypeContext extend(String var, ValueType type) {
		return new VarBindingContext(var, type, this);
	}
	
	public abstract ValueType lookup(String varName);
	
	public boolean isPresent(String varName) {
		if (getNext() == null)
			return false;
		else
			return getNext().isPresent(varName);
	}
	
	public static TypeContext empty() {
		return theEmpty;
	}
	
	private static TypeContext theEmpty = new EmptyTypeContext(); 
	
	protected abstract TypeContext getNext();
}
