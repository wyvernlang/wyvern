package wyvern.target.corewyvernIL.type;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import wyvern.stdlib.support.backend.BytecodeOuterClass;
import wyvern.target.corewyvernIL.BindingSite;
import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.decltype.AbstractTypeMember;
import wyvern.target.corewyvernIL.decltype.ConcreteTypeMember;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.decltype.EffectDeclType;
import wyvern.target.corewyvernIL.effects.EffectSet;
import wyvern.target.corewyvernIL.expression.Path;
import wyvern.target.corewyvernIL.expression.Tag;
import wyvern.target.corewyvernIL.expression.Value;
import wyvern.target.corewyvernIL.generics.GenericArgument;
import wyvern.target.corewyvernIL.generics.GenericKind;
import wyvern.target.corewyvernIL.support.EvalContext;
import wyvern.target.corewyvernIL.support.FailureReason;
import wyvern.target.corewyvernIL.support.SubtypeAssumption;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.support.View;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.HasLocation;
import wyvern.tools.errors.ToolError;

public class RefinementType extends ValueType {
    public RefinementType(ValueType base, List<DeclType> declTypes, HasLocation hasLoc, String selfName) {
        this(base, declTypes, hasLoc, new BindingSite(selfName));
    }
    
    /** Applies the old refinement to this new base
     * 
     * @param base
     * @param old
     */
    public RefinementType(ValueType base, RefinementType old) {
        super(old);
        this.base = base;
        this.selfSite = old.selfSite;
        this.declTypes = old.declTypes;
        this.genericArguments = old.genericArguments;
    }

    public RefinementType(ValueType base, List<DeclType> declTypes, HasLocation hasLoc, BindingSite selfSite) {
        super(hasLoc);
        this.base = base;
        this.declTypes = declTypes;
        this.selfSite = selfSite;
    }

    public RefinementType(List<GenericArgument> genericArguments, ValueType base, HasLocation hasLoc) {
        super(hasLoc);
        this.base = base;
        this.genericArguments = genericArguments;
    }

    private ValueType base;
    private BindingSite selfSite;
    private List<DeclType> declTypes = null; // may be computed lazily from genericArguments
    private List<GenericArgument> genericArguments;

    private List<DeclType> getDeclTypes(TypeContext ctx) {
        if (declTypes == null) {
            declTypes = new LinkedList<DeclType>();
            base.checkWellFormed(ctx);
            StructuralType st = base.getStructuralType(ctx, null);
            if (st == null) {
                ToolError.reportError(ErrorMessage.CANNOT_APPLY_GENERIC_ARGUMENTS, getLocation(), base.toString());
            }

            final List<DeclType> stDeclTypes = st.getDeclTypes();
            final Iterator<GenericArgument> genericArgumentIterator =
                    genericArguments == null ? Collections.emptyIterator() : genericArguments.iterator();

            // Instantiate (make concrete) the abstract types with the arguments that are passed in
            // Note: The case in which there are more generic arguments than declTypes in the structural type
            //       is handled after the loop (in the if statement)
            for (DeclType dt : stDeclTypes) {
                // Generic arguments are used up
                if (!genericArgumentIterator.hasNext()) {
                    break;
                }

                final GenericArgument ga = genericArgumentIterator.next();

                final DeclType declTypeToAdd;
                if (dt instanceof AbstractTypeMember) {
                    if (ga.getKind() != GenericKind.TYPE) {
                        ToolError.reportError(ErrorMessage.NON_TYPE_ARGUMENT, getLocation(), ga.getKind().toString());
                    }
                    declTypeToAdd = new ConcreteTypeMember(dt.getName(), ga.getType());
                } else if (dt instanceof EffectDeclType) {
                    if (ga.getKind() != GenericKind.EFFECT) {
                        ToolError.reportError(ErrorMessage.NON_EFFECT_ARGUMENT, getLocation(), ga.getKind().toString());
                    }
                    declTypeToAdd = new EffectDeclType(dt.getName(), ga.getEffect(), dt.getLocation());
                } else {
                    continue;
                }

                declTypes.add(declTypeToAdd);
            }

            // ...too many generic arguments!
            if (genericArgumentIterator.hasNext()) {
                final GenericArgument ga = genericArgumentIterator.next();
                switch (ga.getKind()) {
                    case TYPE:
                        ToolError.reportError(ErrorMessage.NO_TYPE_MEMBER, getLocation(), ga.getType().toString());
                        break;
                    case EFFECT:
                        ToolError.reportError(ErrorMessage.NO_EFFECT_MEMBER, getLocation(), ga.getEffect().toString());
                        break;
                    default:
                        throw new RuntimeException("Unhandled corewyvernIL generic argument kind: " + ga.getKind());
                }
            }
        }
        return declTypes;
    }

    @Override
    public <S, T> T acceptVisitor(ASTVisitor<S, T> visitor, S state) {
        return visitor.visit(state,  this);
    }

    @Override
    public ValueType adapt(View v) {
        ValueType newBase = base.adapt(v);
        if (declTypes == null) {
            return new RefinementType(
                    genericArguments.stream().map(ga -> ga.adapt(v)).collect(Collectors.toList()),
                    newBase,
                    this
            );
        }
        List<DeclType> newDTs = new LinkedList<DeclType>();
        for (DeclType dt : declTypes) {
            newDTs.add(dt.adapt(v));
        }
        return new RefinementType(newBase, newDTs, this, selfSite);
    }

    @Override
    public ValueType doAvoid(String varName, TypeContext ctx, int depth) {
        List<DeclType> newDeclTypes = new LinkedList<DeclType>();
        boolean changed = false;
        ValueType newBase = base.doAvoid(varName, ctx, depth);
        if (declTypes == null) {
            return new RefinementType(
                    genericArguments.stream()
                            .map(ga -> ga.doAvoid(varName, ctx, depth))
                            .collect(Collectors.toList()),
                    newBase,
                    this
            );
        }
        for (DeclType dt : declTypes) {
            DeclType newDT = dt.doAvoid(varName, ctx, depth + 1);
            newDeclTypes.add(newDT);
            if (newDT != dt) {
                changed = true;
            }
        }
        if (!changed && base == newBase) {
            return this;
        } else {
            return new RefinementType(newBase, newDeclTypes, this, selfSite);
        }
    }

    private static int checkWellFormed = 0;
    
    @Override
    public void checkWellFormed(TypeContext ctx) {
        checkWellFormed++;
        base.checkWellFormed(ctx);
        final TypeContext selfCtx = selfSite == null ? ctx : ctx.extend(selfSite, this);
        for (DeclType dt : getDeclTypes(ctx)) {
            dt.canonicalize(ctx);
            dt.checkWellFormed(selfCtx);
        }
        checkWellFormed--;
    }

    /** Returns the self name if there is one, otherwise null */
    public String getSelfName() {
        if (selfSite == null) {
            return null;
        }
        return selfSite.getName();
    }

    /** Returns the base type of this refinement */
    public ValueType getBase() {
        return base;
    }

    @Override
    public StructuralType getStructuralType(TypeContext ctx, StructuralType theDefault) {
        if (checkWellFormed == 0) {
            // short-circuits an infinite well-formedness check loop
            checkWellFormed(ctx);
        }
        StructuralType baseST = base.getStructuralType(ctx, theDefault);
        List<DeclType> newDTs = new LinkedList<DeclType>();
        int current = 0;
        int max = getDeclTypes(ctx).size();
        for (DeclType t : baseST.getDeclTypes()) {
            List<DeclType> rdts = getDeclTypes(ctx);
            if (current < max && t.getName().equals(rdts.get(current).getName())) {
                newDTs.add(rdts.get(current));
                current++;
            } else {
                newDTs.add(t);
            }
        }
        if (current != max) {
            // TODO: replace with a nice warning
            // throw new RuntimeException("invalid refinement type " + this);

            // this RefinementType was created by "new", therefore just use
            // all the DeclTypes from the RefinementType and none from the base
            newDTs = getDeclTypes(ctx);
        }

        return new StructuralType(baseST.getSelfSite(), newDTs, isResource(ctx));
    }
    @Override
    public boolean isResource(TypeContext ctx) {
        return base.isResource(ctx);
    }
    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[] {base, declTypes});
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof RefinementType)) {
            return false;
        }

        RefinementType other = (RefinementType) obj;

        if (!base.equals(other.base)) {
            return false;
        }

        if (declTypes == null || other.declTypes == null) {
            return getGenericArguments().equals(other.getGenericArguments());
        } else {
            return declTypes.equals(other.declTypes);
        }
    }

    public List<GenericArgument> getGenericArguments() {
        if (genericArguments != null) {
            return genericArguments;
        }
        LinkedList<GenericArgument> result = new LinkedList<>();
        for (DeclType dt : declTypes) {
            if (dt instanceof ConcreteTypeMember) {
                ConcreteTypeMember ctm = (ConcreteTypeMember) dt;
                result.addLast(new GenericArgument(ctm.getRawResultType()));
            } else if (dt instanceof EffectDeclType) {
                EffectDeclType edt = (EffectDeclType) dt;
                EffectSet effectSet = edt.getEffectSet();
                // The effect member is concrete
                if (effectSet != null) {
                    result.addLast(new GenericArgument(edt.getEffectSet()));
                }
            }
        }
        return result;
    }

    @Override
    public boolean isSubtypeOf(ValueType t, TypeContext ctx, FailureReason reason) {
        // if they are equivalent to a DynamicType or equal to us, then return true
        if (equals(t)) {
            return true;
        }
        if (ctx.isAssumedSubtype(this, t)) {
            return true;
        }
        ctx = new SubtypeAssumption(this, t, ctx);
        final ValueType ct = t.getCanonicalType(ctx);
        if (super.isSubtypeOf(ct, ctx, new FailureReason())) {
            return true;
        }

        // if their canonical type is a NominalType, check if our base is a subtype of it
        if (ct instanceof NominalType) {
            return base.isSubtypeOf(ct, ctx, reason);
        }

        // if their canonical type is a RefinementType, compare the bases (for any tags) and separately check the structural types
        if (ct instanceof RefinementType) {
            if (!base.isSubtypeOf(((RefinementType) ct).base, ctx, reason)) {
                return false;
            }
        }
        // compare structural types
        return this.getStructuralType(ctx).isSubtypeOf(ct.getStructuralType(ctx), ctx, reason);
    }

    @Override
    public ValueType getCanonicalType(TypeContext ctx) {
        ValueType baseCT = base.getCanonicalType(ctx);
        if (baseCT instanceof StructuralType) {
            return this.getStructuralType(ctx);
        } else {
            return this;
        }
    }

    @Override
    public void doPrettyPrint(Appendable dest, String indent, TypeContext ctx) throws IOException {
        base.doPrettyPrint(dest, indent, ctx);
        dest.append('[');

        if (declTypes != null) {
            // limitation: would be better to actually print the DeclTypes that aren't ConcreteTypeMembers as part of the underlying type

            String delim = "";
            for (DeclType dt : declTypes) {
                dest.append(delim);

                if (dt instanceof ConcreteTypeMember) {
                    ((ConcreteTypeMember) dt).getRawResultType().doPrettyPrint(dest, indent, ctx);
                } else if (dt instanceof EffectDeclType) {
                    dest.append(((EffectDeclType) dt).getEffectSet().toString());
                }

                delim = ", ";
            }
        } else {
            String delim = "";
            for (GenericArgument ga : genericArguments) {
                dest.append(delim);

                switch (ga.getKind()) {
                    case TYPE:
                        ga.getType().doPrettyPrint(dest, indent, ctx);
                        break;
                    case EFFECT:
                        dest.append(ga.getEffect().toString());
                        break;
                    default:
                        throw new RuntimeException("Unhandled corewyvernIL generic argument kind: " + ga.getKind());
                }

                delim = ", ";
            }
        }

        dest.append(']');
    }

    @Override
    public BytecodeOuterClass.Type emitBytecodeType() {
        BytecodeOuterClass.Type base = getBase().emitBytecodeType();

        // TODO: resourceFlag
        // boolean resourceFlag = isResource(null);
        boolean resourceFlag = false;

        String selfName = getSelfName();
        if (selfName == null) {
            selfName = "null";
        }

        BytecodeOuterClass.Type.CompoundType.Builder ct = BytecodeOuterClass.Type.CompoundType.newBuilder()
                .setBase(base).setSelfName(selfName).setStateful(resourceFlag);

        if (declTypes != null) {
            for (DeclType dt : declTypes) {
                ct.addDeclTypes(dt.emitBytecode());
            }
        }

        return BytecodeOuterClass.Type.newBuilder().setCompoundType(ct).build();
    }

    @Override
    public Path getPath() {
        return base.getPath();
    }
    
    @Override
    public Value getMetadata(TypeContext ctx) {
        return base.getMetadata(ctx);
    }

    @Override
    public Tag getTag(EvalContext ctx) {
        return base.getTag(ctx);
    }

    @Override
    public boolean isTagged(TypeContext ctx) {
        return base.isTagged(ctx);
    }


    @Override
    public boolean isEffectAnnotated(TypeContext ctx) {
        return this.getBase().isEffectAnnotated(ctx);
    }

    @Override
    public boolean isEffectUnannotated(TypeContext ctx) {
        return this.getBase().isEffectUnannotated(ctx)
                && this.getGenericArguments().stream().allMatch(ga -> ga.getKind() != GenericKind.EFFECT);
    }
    /**
     * Can be instantiated if the base type can.
     */
    @Override
    public void canInstantiate(TypeContext ctx) {
        base.canInstantiate(ctx);
    }
}
