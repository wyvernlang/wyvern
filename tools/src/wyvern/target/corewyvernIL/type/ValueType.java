package wyvern.target.corewyvernIL.type;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import wyvern.stdlib.support.backend.BytecodeOuterClass;
import wyvern.target.corewyvernIL.IASTNode;
import wyvern.target.corewyvernIL.decltype.DeclType;
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
            return dest.toString();
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
        BytecodeOuterClass.TypeDesc.CaseDesc.Builder cd = BytecodeOuterClass.TypeDesc.CaseDesc.newBuilder().setType(emitBytecodeType());
        return BytecodeOuterClass.TypeDesc.newBuilder().setCaseDesc(cd).build();
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
        return doAvoid(varName, ctx, 0);
    }
    public static final int MAX_RECURSION_DEPTH = 10;
    public Tag getTag(EvalContext ctx) {
        throw new RuntimeError("internal error: getTag not implemented for things other than nominal types");
    }
}
