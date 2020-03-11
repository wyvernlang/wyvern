package wyvern.target.corewyvernIL.type;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

import wyvern.stdlib.support.backend.BytecodeOuterClass;
import wyvern.target.corewyvernIL.IASTNode;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.expression.Path;
import wyvern.target.corewyvernIL.expression.Tag;
import wyvern.target.corewyvernIL.expression.Value;
import wyvern.target.corewyvernIL.support.EvalContext;
import wyvern.target.corewyvernIL.support.FailureReason;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.support.View;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.HasLocation;
import wyvern.tools.errors.RuntimeError;

public abstract class ValueType extends Type implements IASTNode {
    @Override
    public boolean isTSubtypeOf(Type sourceType, TypeContext ctx, FailureReason reason) {
        if (sourceType instanceof ValueType) {
            return isSubtypeOf((ValueType) sourceType, ctx, reason);
        }
        return false;
    }
    public ValueType() { }
    public ValueType(HasLocation hasLoc) {
        super(hasLoc);
    }
    public ValueType(FileLocation loc) {
        super(loc);
    }
    /**
     * Returns the equivalent structural type.  If the structural type
     * is unknown (e.g. because this is a nominal type and the
     * corresponding type member is abstract from this context) then
     * the empty structural type is returned.
     *
     * @param ctx TODO
     */
    public final StructuralType getStructuralType(TypeContext ctx) {
        return getStructuralType(ctx, StructuralType.getEmptyType());
    }

    public final String desugar(TypeContext ctx) {
        try {
            Appendable dest = new StringBuilder();
            doPrettyPrint(dest, "", ctx);
            return dest.toString().replaceAll("MOD\\$", "");
        } catch (IOException e) {
            e.printStackTrace();
            return "ERROR_PRINTING";
        }
    }

    /**
     * If ctx is non-null, then we try to desugar the type.
     * Otherwise this is the same as regular pretty-printing.
     */
    public abstract void doPrettyPrint(Appendable dest, String indent, TypeContext ctx) throws IOException;

    public Path getPath() {
        return null;
    }
    
    public final void doPrettyPrint(Appendable dest, String indent) throws IOException {
        doPrettyPrint(dest, indent, null);
    }

    // :BytecodeTypeHack
    // Hack: Type (T in the bytecode) and ValueType (\tau in bytecode) have different bytecode representations
    // but are in the same inheritance tree. Hence, for Type with have emitBytecodeTypeDesc() and in ValueType
    // we have emitBytecodeType().
    public BytecodeOuterClass.Type emitBytecodeType() {
        System.out.println("emitBytecode not implemented for " + this.getClass().getName());
        throw new java.lang.UnsupportedOperationException("Not yet implemented");
    }

    // Sometimes, we want to generate a TypeDesc for a ValueType so we handle creating an untagged TypeDesc here
    @Override
    public final BytecodeOuterClass.TypeDesc emitBytecodeTypeDesc() {
        return BytecodeOuterClass.TypeDesc.newBuilder().setType(emitBytecodeType()).build();
    }

    @Override
    public NominalType getParentType(View v) {
        return null;
    }

    /**
     * Returns the equivalent structural type.  If the structural type
     * is unknown (e.g. because this is a nominal type and the
     * corresponding type member is abstract from this context) then
     * the default type is returned
     *
     * @param ctx TODO
     */
    public StructuralType getStructuralType(TypeContext ctx, StructuralType theDefault) {
        return theDefault;
    }

    /** For nominal types that are transitively equivalent to a known type, return that type.
     *  For all other types, this is the identity.
     */
    public ValueType getCanonicalType(TypeContext ctx) {
        return this;
    }

    public boolean isResource(TypeContext ctx) {
        return false;
    }

    public boolean isSubtypeOf(ValueType t, TypeContext ctx, FailureReason reason) {
        return t instanceof DynamicType || equals(t); // default
    }

    /** Find the declaration type with the specified name, excluding matches for which the exclusionFilter returns true, or return null if it is not present */
    public DeclType findMatchingDecl(String name, Predicate<? super DeclType> exclusionFilter, TypeContext ctx) {
        StructuralType st = getStructuralType(ctx);
        if (st == null) {
            return null;
        }
        return st.findMatchingDecl(name, exclusionFilter, ctx);
    }
    
    /** Find the declaration type with the specified name, or return null if it is not present */
    public DeclType findDecl(String declName, TypeContext ctx) {
        StructuralType st = getStructuralType(ctx);
        if (st == null) {
            return null;
        }
        return st.findDecl(declName, ctx);
    }

    /** Find the declaration type with the specified name, or return null if it is not present */
    @SuppressWarnings("unchecked")
    public List<DeclType> findDecls(String declName, TypeContext ctx) {
        StructuralType st = getStructuralType(ctx);
        if (st == null) {
            return (List<DeclType>) Collections.EMPTY_LIST;
        }
        return st.findDecls(declName, ctx);
    }

    @Override
    public abstract ValueType adapt(View v);

    @Override
    public abstract ValueType doAvoid(String varName, TypeContext ctx, int depth);

    public boolean equalsInContext(ValueType otherType, TypeContext ctx, FailureReason reason) {
        return this.isSubtypeOf(otherType, ctx, reason) && otherType.isSubtypeOf(this, ctx, reason);
    }

    /**
     * Evaluates any metadata that might be present in this type to a value
     */
    public ValueType interpret(EvalContext ctx) {
        return this;
    }

    /**
     * Gets the metadata, if any, for this type.
     * Returns null if no metadata is associated with this type.
     */
    public Value getMetadata(TypeContext ctx) {
        return null;
    }

    @Override
    public ValueType getValueType() {
        return this;
    }

    /**
     * Returns this type, avoiding the named variable if possible
     * @param count TODO
     */
    public final ValueType avoid(String varName, TypeContext ctx) {
        return doAvoid(varName, ctx, INIT_RECURSION_DEPTH);
    }
    public static final int INIT_RECURSION_DEPTH = 0;
    public static final int MAX_RECURSION_DEPTH = 10;
    public Tag getTag(EvalContext ctx) {
        throw new RuntimeError("internal error: getTag not implemented for things other than nominal types");
    }

    /**
     * If a type annotates all of its methods, it is considered effect-annotated. If a type does not annotate any of its
     * methods AND it does not declare any new effects or reference any existing effects, it is considered
     * effect-unannotated. These conditions are not mutually exclusive. For example:
     *
     * Some types that are effect-annotated and not effect-unannotated:
     *
     *   type T
     *     def foo() : {system.FFI} Unit
     *
     *   type U
     *     effect E [ = ... ]
     *
     * A type that is effect-unannotated and not effect-annotated:
     *
     *   type T
     *     def bar() : Unit
     *
     * A type that is both effect-annotated and effect-unannotated:
     *
     *   type T
     *     val x : Int
     *
     * Some types that are neither effect-annotated nor effect-unannotated
     *
     *   type T
     *     def foo() : {system.FFI} Unit
     *     def bar() : Unit
     *
     *   type U
     *     def bar() : Unit
     *     effect E [ = ... ]
     *
     * As such, !isEffectAnnotated() is NOT the same as isEffectUnannotated().
     *
     * TODO: Craig et al. import semantics
     *   We allow effect-unannotated types to reference effect-annotated types. To ensure that this is safe, we must
     *   implement Craig et al.'s import semantics so that the effect-unannotated code cannot violate the type
     *   signatures of any of the effect-annotated code. For now, we assume that these import semantics hold.
     *
     * TODO: Quantification lifting
     *   All references to effect-unannotated types in an effect-annotated type will undergo quantification lifting,
     *   thus ensuring that the effect-unannotated types can safely be treated as effect-annotated.
     *
     * @param ctx The type context to look in
     * @return True if the type is effect-annotated, false otherwise.
     */
    public abstract boolean isEffectAnnotated(TypeContext ctx);

    /**
     * See the {@link #isEffectAnnotated()} method documentation for a description of what counts as effect-annotated
     * and what counts as effect-unannotated. In particular, !isEffectUnannotated() is NOT the same as
     * isEffectAnnotated().
     *
     * @param ctx The type context to look in
     * @return True if the type is effect-unannotated, false otherwise.
     */
    public abstract boolean isEffectUnannotated(TypeContext ctx);
    /**
     * Checks if it is legal to instantiate this type.  Does nothing for most types.
     * But for tagged types, there must not be a "comprises" limitation.
     * Assumes this check has already been checked for well-formedness.
     * @param ctx
     */
    public void canInstantiate(TypeContext ctx) {
    }
}
