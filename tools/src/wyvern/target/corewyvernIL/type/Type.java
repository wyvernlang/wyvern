package wyvern.target.corewyvernIL.type;

import wyvern.stdlib.support.backend.BytecodeOuterClass;
import wyvern.target.corewyvernIL.ASTNode;
import wyvern.target.corewyvernIL.IASTNode;
import wyvern.target.corewyvernIL.support.FailureReason;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.support.View;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.HasLocation;

public abstract class Type extends ASTNode implements IASTNode {
    public Type() { }
    public Type(HasLocation hasLoc) {
        super(hasLoc);
    }
    public Type(FileLocation loc) {
        super(loc);
    }
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
    public abstract void checkWellFormed(TypeContext ctx);

    public BytecodeOuterClass.TypeDesc emitBytecodeTypeDesc() {
        System.out.println("emitBytecode not implemented for " + this.getClass().getName());
        throw new java.lang.UnsupportedOperationException("Not yet implemented");
    }

    // TODO: depth limit is hacky, find a more principled approach to avoidance
    public abstract Type doAvoid(String varName, TypeContext ctx, int depth);
    public abstract boolean isTagged(TypeContext ctx);
    public abstract boolean isTSubtypeOf(Type sourceType, TypeContext ctx, FailureReason reason);
    
    public static boolean typesEquiv(ValueType t1, ValueType t2, TypeContext ctx, FailureReason reason) {
        if (t1 == null && t2 == null) {
            return true;
        }
        if (t1 == null || t2 == null) {
            // one but not both is null
            return false;
        }
        return t1.isSubtypeOf(t2, ctx, reason) && t2.isSubtypeOf(t1, ctx, reason);
    }
}
