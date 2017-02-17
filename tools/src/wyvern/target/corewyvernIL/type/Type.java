package wyvern.target.corewyvernIL.type;

import wyvern.target.corewyvernIL.ASTNode;
import wyvern.target.corewyvernIL.IASTNode;
import wyvern.target.corewyvernIL.support.TypeContext;

public abstract class Type extends ASTNode implements IASTNode {
	public abstract ValueType getValueType(); 
	
	/**
	 * Checks if this type is well-formed, throwing an exception if not
	 */
	abstract public void checkWellFormed(TypeContext ctx);
	
}
