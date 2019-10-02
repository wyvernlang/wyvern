package wyvern.target.corewyvernIL.decltype;

import wyvern.stdlib.support.backend.BytecodeOuterClass;
import wyvern.target.corewyvernIL.ASTNode;
import wyvern.target.corewyvernIL.IASTNode;
import wyvern.target.corewyvernIL.expression.Value;
import wyvern.target.corewyvernIL.support.EvalContext;
import wyvern.target.corewyvernIL.support.FailureReason;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.support.View;

public abstract class DeclType extends ASTNode implements IASTNode {
    private String name;

    DeclType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract boolean isSubtypeOf(DeclType dt, TypeContext ctx, FailureReason reason);

    public abstract DeclType adapt(View v);
    /**
     * 
     * @param ctx
     * @return true iff this is a var, or if this is a val that is of resource type
     */
    public boolean containsResource(TypeContext ctx) {
        return false;
    }

    /**
     * Evaluates any metadata that might be present in this type to a value
     */
    public DeclType interpret(EvalContext ctx) {
        return this;
    }

    /**
     * Gets the metadata, if any, for this DeclType.
     * Returns null if no metadata is associated with this DeclType.
     */
    public Value getMetadataValue() {
        return null;
    }

    public abstract void checkWellFormed(TypeContext ctx);

    /**
     * Avoids the specified variable.  Returns the original DeclType object
     * if the variable was not used.
     * @param count TODO
     */
    public abstract DeclType doAvoid(String varName, TypeContext ctx, int count);

    public abstract boolean isTypeOrEffectDecl();

    public BytecodeOuterClass.DeclType emitBytecode() {
        System.out.println("emitBytecode not implemented for " + this.getClass().getName());
        throw new java.lang.UnsupportedOperationException("Not yet implemented");
    }

    /**
     * See the {@link wyvern.target.corewyvernIL.type.ValueType#isEffectAnnotated()} method documentation for a
     * description of what counts as effect-annotated and what counts as effect-unannotated. In particular,
     * !isEffectAnnotated() is NOT the same as isEffectUnannotated().
     *
     * @param ctx The type context to look in
     * @return True if the type is effect-annotated, false otherwise.
     */
    public abstract boolean isEffectAnnotated(TypeContext ctx);

    /**
     * See the {@link wyvern.target.corewyvernIL.type.ValueType#isEffectAnnotated()} method documentation for a
     * description of what counts as effect-annotated and what counts as effect-unannotated. In particular,
     * !isEffectUnannotated() is NOT the same as isEffectAnnotated().
     *
     * @param ctx The type context to look in
     * @return True if the type is effect-unannotated, false otherwise.
     */
    public abstract boolean isEffectUnannotated(TypeContext ctx);

    /** for effect declarations, makes sure the variables are bound */
    public void canonicalize(TypeContext ctx) {
        // default implementation does nothing
    }
}
