package wyvern.target.corewyvernIL.type;

import wyvern.target.corewyvernIL.ASTNode;
import wyvern.target.corewyvernIL.IASTNode;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.support.View;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.HasLocation;

public abstract class Type extends ASTNode implements IASTNode {
    public Type() {}
    public Type(HasLocation hasLoc) { super(hasLoc); }
    public Type(FileLocation loc) { super(loc); }
	public abstract ValueType getValueType(); 
	public abstract NominalType getParentType(View view);
	
	/**
	 * Returns a type that is equivalent to this type
	 * under the View v.  If v maps x to y.f, for example,
	 * then a type of the form x.g.T will be mapped to the
	 * type y.f.g.T
	 */
	public abstract Type adapt(View v);

	/**
	 * Checks if this type is well-formed, throwing an exception if not
	 */
	abstract public void checkWellFormed(TypeContext ctx);
	
	// TODO: depth limit is hacky, find a more principled approach to avoidance
	abstract public Type doAvoid(String varName, TypeContext ctx, int depth);
}
