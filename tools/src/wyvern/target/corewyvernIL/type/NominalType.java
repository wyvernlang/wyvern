package wyvern.target.corewyvernIL.type;

import java.io.IOException;
import java.util.Arrays;

import wyvern.stdlib.support.backend.BytecodeOuterClass;
import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.decl.TypeDeclaration;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.decltype.ConcreteTypeMember;
import wyvern.target.corewyvernIL.decltype.DefinedTypeMember;
import wyvern.target.corewyvernIL.decltype.AbstractTypeMember;
import wyvern.target.corewyvernIL.expression.ObjectValue;
import wyvern.target.corewyvernIL.expression.Path;
import wyvern.target.corewyvernIL.expression.Tag;
import wyvern.target.corewyvernIL.expression.Value;
import wyvern.target.corewyvernIL.expression.Variable;
import wyvern.target.corewyvernIL.support.EvalContext;
import wyvern.target.corewyvernIL.support.FailureReason;
import wyvern.target.corewyvernIL.support.SubtypeAssumption;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.support.Util;
import wyvern.target.corewyvernIL.support.View;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.RuntimeError;
import wyvern.tools.errors.ToolError;

public class NominalType extends ValueType {
    private Path path;
    private String typeMember;

    public NominalType(String pathVariable, String typeMember) {
        this (new Variable(pathVariable), typeMember, null);
        Util.check(pathVariable != null && typeMember != null, "null pointer");
    }

    public NominalType(Path path, String typeMember) {
        this(path, typeMember, null);
        Util.check(path != null && typeMember != null, "null pointer");
    }
    public NominalType(Path path, String typeMember, FileLocation location) {
        super(location);
        Util.check(path != null && typeMember != null, "null pointer");
        this.path = path;
        this.typeMember = typeMember;
    }

    @Override
    public Path getPath() {
        return path;
    }

    public String getTypeMember() {
        return typeMember;
    }

    @Override
    public boolean isResource(TypeContext ctx) {
        DeclType dt = getSourceDeclType(ctx);
        if (dt instanceof DefinedTypeMember) {
            ValueType vt = ((DefinedTypeMember) dt).getResultType(View.from(path, ctx));
            if (vt == this) {
                ToolError.reportError(ErrorMessage.RECURSIVE_TYPE, dt, typeMember);
            }
            return vt.isResource(ctx);
        } else {
            return ((AbstractTypeMember) dt).isResource();
        }
    }

    @Override
    public StructuralType getStructuralType(TypeContext ctx, StructuralType theDefault) {
        DeclType dt = null;
        try {
            dt = getSourceDeclType(ctx);
        } catch (RuntimeException e) {
            return super.getStructuralType(ctx, theDefault); // can't look up a structural type
        }
        if (dt instanceof DefinedTypeMember) {
            ValueType vt = ((DefinedTypeMember) dt).getResultType(View.from(path, ctx));
            if (vt.equals(this)) {
                return theDefault;
            }
            return vt.getStructuralType(ctx, theDefault);
        } else {
            return super.getStructuralType(ctx, theDefault);
        }
    }

    private static int nestingCount = 0;
    
    @Override
    public boolean isTSubtypeOf(Type sourceType, TypeContext ctx, FailureReason reason) {
        if (super.isTSubtypeOf(sourceType, ctx, reason)) {
            return true;
        }
        // try looking up my source decl
        DeclType t = getSourceDeclType(ctx);
        if (t instanceof ConcreteTypeMember) {
            ConcreteTypeMember ctm = (ConcreteTypeMember) t;
            return ctm.getSourceType().isTSubtypeOf(sourceType, ctx, reason);
        }
        return false;
    }

    
    DeclType getSourceDeclType(TypeContext ctx) {
        final ValueType t = path.typeCheck(ctx, null);
        synchronized (NominalType.class) {
            nestingCount++;
            if (nestingCount > 100) {
                // check for excessive recursion failed
                throw new RuntimeException("Internal error: recursion");
            }
        }
        final StructuralType structuralType = t.getStructuralType(ctx);
        synchronized (NominalType.class) {
            nestingCount--;
        }
        // return any DefinedTypeMember or AbstractTypeMember
        return structuralType.findMatchingDecl(typeMember, cdt -> !(cdt instanceof DefinedTypeMember || cdt instanceof AbstractTypeMember), ctx);
    }

    @Override
    public ValueType getCanonicalType(TypeContext ctx) {
        DeclType dt = null;
        try {
            dt = getSourceDeclType(ctx);
        } catch (RuntimeException e) {
            // failed to get a canonical type
            return this;
        }
        if (dt instanceof ConcreteTypeMember) {
            if (((ConcreteTypeMember) dt).getSourceType() instanceof TagType) {
                return this;
            }
            final ValueType resultType = ((ConcreteTypeMember) dt).getResultType(View.from(path, ctx));
            if (this.equals(resultType)) {
                return this;
            } else {
                return resultType.getCanonicalType(ctx);
            }
        } else {
            return this;
        }
    }

    public NominalType getCanonicalNominalType(TypeContext ctx) {
        DeclType dt = null;
        try {
            dt = getSourceDeclType(ctx);
        } catch (RuntimeException e) {
            // failed to get a canonical type
            return this;
        }
        if (dt instanceof ConcreteTypeMember) {
            if (((ConcreteTypeMember) dt).getSourceType() instanceof TagType) {
                return this;
            }
            final ValueType resultType = ((ConcreteTypeMember) dt).getResultType(View.from(path, ctx));
            if (this.equals(resultType) || !(resultType instanceof NominalType)) {
                return this;
            } else {
                return ((NominalType) resultType).getCanonicalNominalType(ctx);
            }
        } else {
            return this;
        }
    }

    @Override
    public void doPrettyPrint(Appendable dest, String indent, TypeContext ctx) throws IOException {
        String desugared = null;
        if (ctx != null) {
            if (path instanceof Variable) {
                desugared = ctx.desugarType(path, typeMember);
            }
        }
        if (desugared == null) {
            path.doPrettyPrint(dest, indent);
            dest.append('.').append(typeMember);
        } else {
            dest.append(desugared);
        }
    }

    private static final NominalType topType = new NominalType("system", "Unit");
    private static final NominalType dynType = new NominalType("system", "Dyn");
    
    @Override
    public BytecodeOuterClass.Type emitBytecodeType() {
        if (equals(topType)) {
            return BytecodeOuterClass.Type.newBuilder().setSimpleType(BytecodeOuterClass.Type.SimpleType.Top).build();
        } else if (equals(dynType)) {
            return BytecodeOuterClass.Type.newBuilder().setSimpleType(BytecodeOuterClass.Type.SimpleType.Dyn).build();
        } else {
            return BytecodeOuterClass.Type.newBuilder().setPath(getPath() + "." + getTypeMember()).build();
        }
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[] {path, typeMember});
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof NominalType)) {
            return false;
        }
        NominalType other = (NominalType) obj;
        return path.equals(other.path) && typeMember.equals(other.typeMember);
    }

    @Override
    public boolean isSubtypeOf(ValueType t, TypeContext ctx, FailureReason reason) {
        // check if they are the same type
        if (super.isSubtypeOf(t, ctx, new FailureReason())) {
            return true;
        }
        if (t instanceof BottomType) {
            return false;
        }
        if (ctx.isAssumedSubtype(this, t)) {
            return true;
        }

        DeclType dt = getSourceDeclType(ctx);

        if (dt instanceof ConcreteTypeMember) {
            Type definedType = ((ConcreteTypeMember) dt).getSourceType();
            ValueType ct = t.getCanonicalType(ctx);

            if (definedType instanceof TagType) {
                // before checking parent, test for equality with canonical type
                if (super.isSubtypeOf(ct, ctx, new FailureReason())) {
                    return true;
                }
                NominalType superType = ((TagType) definedType).getParentType(View.from(path, ctx));
                // TODO: this is not necessarily the whole check, but it does the nominal part of the check correctly
                if (superType == null) {
                    if (reason != null && !reason.isDefined()) {
                        reason.setReason("nominal type " + this.desugar(ctx) + " has no supertype and thus can't be a subtype of " + t.desugar(ctx));
                    }
                    return false;
                }
                return superType.isSubtypeOf(t, ctx, reason);
            }
            ValueType vt = ((ConcreteTypeMember) dt).getResultType(View.from(path, ctx));

            // if t is nominal but vt and ct are structural, assume this <: t in subsequent checking
            //if (t instanceof NominalType && ct instanceof StructuralType && vt instanceof StructuralType)
            ctx = new SubtypeAssumption(this, t, ctx);
            return vt.isSubtypeOf(ct, ctx, reason);
        /*} else if (dt instanceof TaggedTypeMember) {
            Type typeDefn = ((TaggedTypeMember) dt).getTypeDefinition(View.from(path, ctx));
            NominalType superType = typeDefn.getParentType(View.from(path, ctx));
            // TODO: this is not necessarily the whole check, but it does the nominal part of the check correctly
            return superType == null ? false : superType.isSubtypeOf(t, ctx, reason);*/
        } else {
            ValueType ct = t.getCanonicalType(ctx);
            // check for equality with the canonical type
            if (super.isSubtypeOf(ct, ctx, reason)) {
                return true;
            } else {
                // report error in terms of original type
                if (!reason.isDefined()) {
                    reason.setReason("type " + this + " is abstract and cannot be checked to be a subtype of " + t);
                }
                return false;
            }
        }
    }

    @Override
    public <S, T> T acceptVisitor(ASTVisitor<S, T> emitILVisitor, S state) {
        return emitILVisitor.visit(state, this);
    }

    @Override
    public ValueType adapt(View v) {
        if (v == null) {
            return this;
        }
        try {
            final Path newPath = path.adapt(v);
            if (newPath == this.path) {
                return this;
            } else {
                return new NominalType(newPath, typeMember);
            }
        } catch (RuntimeException e) {
            if (v.getContext() != null) {
                return getCanonicalType(v.getContext());
            } else {
                throw e;
            }
        }
    }

    @Override
    public Value getMetadata(TypeContext ctx) {
        DeclType t = getSourceDeclType(ctx);
        
        // If one requests invalid or undefined member, then t would be null here, hence this check:
        this.checkWellFormed(ctx);
                
        return t.getMetadataValue();
    }

    @Override
    public void checkWellFormed(TypeContext ctx) {
        // we are well-formed as long as we can get this without an error, and it doesn't return null but instead a well-formed type member
        final DeclType sourceDeclType = this.getSourceDeclType(ctx);
        if (sourceDeclType == null) {
            ToolError.reportError(ErrorMessage.NO_SUCH_TYPE_MEMBER, this, typeMember);
        }
        // would like to check this but can't, to avoid infinite recursion; other code should check it is well-formed
        //sourceDeclType.checkWellFormed(ctx);
    }

    @Override
    public ValueType doAvoid(String varName, TypeContext ctx, int count) {
        if (count > MAX_RECURSION_DEPTH) {
            // best effort failed
            // TODO: make this more principled
            return this;
        }
        if (path.getFreeVariables().contains(varName)) {
            try {
                DeclType dt = this.getSourceDeclType(ctx);
                if (dt instanceof ConcreteTypeMember) {
                    final ValueType type = ((ConcreteTypeMember) dt).getResultType(View.from(path, ctx));
                    if (type.equals(this)) {
                        // avoid infinite loops, just in case
                        // TODO: make this more principled
                        return this;
                    }
                    return type.doAvoid(varName, ctx, count + 1);
                }
            } catch (RuntimeException e) {
                // exception while trying to avoid; fall through to returning "this"
            }
            // was best effort anyway
            // TODO: be more principled
            return this;
        } else {
            return this;
        }
    }

    @Override
    public boolean isTagged(TypeContext ctx) {
        DeclType dt = this.getSourceDeclType(ctx);
        return (dt instanceof ConcreteTypeMember) && ((ConcreteTypeMember) dt).getSourceType().isTagged(ctx);
    }

    @Override
    public Tag getTag(EvalContext ctx) {
        Value v = this.getPath().interpret(ctx);
        if (!(v instanceof ObjectValue)) {
            throw new RuntimeError("internal invariant: can only get the tag of part of an object, did this typecheck?");
        }
        ObjectValue object = (ObjectValue) v;
        TypeDeclaration decl = (TypeDeclaration) object.findDecl(this.getTypeMember(), true);
        if (decl.getSourceType() instanceof NominalType) {
            return ((NominalType) decl.getSourceType()).getTag(object.getEvalCtx());
        }
        return new Tag(object, this.getTypeMember(), this.getPath().toString());
    }

    // Safely true because of quantification lifting
    @Override
    public boolean isEffectAnnotated(TypeContext ctx) {
        return true;
    }

    // Safely true because of Craig et al.'s import semantics
    @Override
    public boolean isEffectUnannotated(TypeContext ctx) {
        return true;
    }

    @Override
    public void canInstantiate(TypeContext ctx) {
        DeclType dt = this.getSourceDeclType(ctx);
        if (dt instanceof ConcreteTypeMember) {
            Type t = ((ConcreteTypeMember) dt).getSourceType();
            if (t instanceof DataType) {
                ToolError.reportError(ErrorMessage.ILLEGAL_TAG_INSTANCE, this, this.desugar(ctx));
            }
        }
    }
    
    public boolean nominallyEquals(NominalType parentType, TypeContext ctx) {
        NominalType myCanonical = getCanonicalNominalType(ctx);
        NominalType theirCanonical = getCanonicalNominalType(ctx);
        
        return myCanonical.equals(theirCanonical);
    }
}
