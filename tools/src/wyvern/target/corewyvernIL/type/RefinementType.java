package wyvern.target.corewyvernIL.type;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.decltype.AbstractTypeMember;
import wyvern.target.corewyvernIL.decltype.ConcreteTypeMember;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.expression.Value;
import wyvern.target.corewyvernIL.support.FailureReason;
import wyvern.target.corewyvernIL.support.SubtypeAssumption;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.support.View;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.HasLocation;
import wyvern.tools.errors.ToolError;

public class RefinementType extends ValueType {
    public RefinementType(ValueType base, List<DeclType> declTypes, HasLocation hasLoc, String selfName) {
        this.base = base;
        this.declTypes = declTypes;
        this.selfName = selfName;
    }

    public RefinementType(List<ValueType> typeParams, ValueType base, HasLocation hasLoc) {
        super(hasLoc);
        this.base = base;
        this.typeParams = typeParams;
    }

    private ValueType base;
    private String selfName;
    private List<DeclType> declTypes = null; // may be computed lazily from typeParams
    private List<ValueType> typeParams;

    private List<DeclType> getDeclTypes(TypeContext ctx) {
        if (declTypes == null) {
            declTypes = new LinkedList<DeclType>();
            base.checkWellFormed(ctx);
            StructuralType st = base.getStructuralType(ctx, null);
            if (st == null) {
                // for debugging
                base.checkWellFormed(ctx);

                ToolError.reportError(ErrorMessage.CANNOT_APPLY_TYPE_PARAMETERS, this.getLocation(), base.toString());
            }
            int index = 0;
            int declCount = st.getDeclTypes().size();
            for (ValueType vt : typeParams) {
                // advance the index to an AbstractTypeMember
                while (index < declCount && !(st.getDeclTypes().get(index) instanceof AbstractTypeMember)) {
                    index++;
                }
                // add a corresponding ConcreteTypeMember
                if (index >= declCount) {
                    ToolError.reportError(ErrorMessage.NO_TYPE_MEMBER, this.getLocation());
                }
                AbstractTypeMember m = (AbstractTypeMember) st.getDeclTypes().get(index);
                declTypes.add(new ConcreteTypeMember(m.getName(), vt));
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
            return new RefinementType(typeParams.stream().map(t -> t.adapt(v)).collect(Collectors.toList()), newBase, this);
        }
        List<DeclType> newDTs = new LinkedList<DeclType>();
        for (DeclType dt : declTypes) {
            newDTs.add(dt.adapt(v));
        }
        return new RefinementType(newBase, newDTs, this, selfName);
    }

    @Override
    public ValueType doAvoid(String varName, TypeContext ctx, int depth) {
        List<DeclType> newDeclTypes = new LinkedList<DeclType>();
        boolean changed = false;
        ValueType newBase = base.doAvoid(varName, ctx, depth);
        if (declTypes == null) {
            List<ValueType> newTPs = typeParams.stream().map(p -> p.doAvoid(varName, ctx, depth)).collect(Collectors.toList());
            return new RefinementType(newTPs, base, this);
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
            return new RefinementType(newBase, newDeclTypes, this, selfName);
        }
    }

    @Override
    public void checkWellFormed(TypeContext ctx) {
        base.checkWellFormed(ctx);
        final TypeContext selfCtx = selfName == null ? ctx : ctx.extend(selfName, this);
        for (DeclType dt : getDeclTypes(ctx)) {
            dt.checkWellFormed(selfCtx);
        }
    }

    /** Returns the self name if there is one, otherwise null */
    public String getSelfName() {
        return selfName;
    }

    @Override
    public StructuralType getStructuralType(TypeContext ctx, StructuralType theDefault) {
        StructuralType baseST = base.getStructuralType(ctx, theDefault);
        List<DeclType> newDTs = new LinkedList<DeclType>();
        int current = 0;
        int max = getDeclTypes(ctx).size();
        for (DeclType t : baseST.getDeclTypes()) {
            if (current < max && t.getName().equals(getDeclTypes(ctx).get(current).getName())) {
                newDTs.add(getDeclTypes(ctx).get(current));
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

        return new StructuralType(baseST.getSelfName(), newDTs, isResource(ctx));
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
        if (declTypes == null && other.declTypes == null) {
            return base.equals(other.base) && typeParams.equals(other.typeParams);
        }
        if (declTypes == null || other.declTypes == null) {
            if (!base.equals(other.base)) {
                return false;
            }
            return countRefinements() == other.countRefinements() && getParamList().equals(other.getParamList());
        }
        return base.equals(other.base) && declTypes.equals(other.declTypes);
    }

    private int countRefinements() {
        return (declTypes != null) ? declTypes.size() : typeParams.size();
    }

    private List<ValueType> getParamList() {
        if (typeParams != null) {
            return typeParams;
        }
        LinkedList<ValueType> result = new LinkedList<ValueType>();
        for (DeclType dt : declTypes) {
            if (dt instanceof ConcreteTypeMember) {
                ConcreteTypeMember ctm = (ConcreteTypeMember) dt;
                result.addLast(ctm.getRawResultType());
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
        dest.append("[");
        int count = 0;
        if (declTypes != null) {
            for (DeclType ctm: declTypes) {
                // limitation: would be better to actually print the DeclTypes that aren't ConcreteTypeMembers as part of the underlying type
                if (ctm instanceof ConcreteTypeMember) {
                    ((ConcreteTypeMember) ctm).getRawResultType().doPrettyPrint(dest, indent, ctx);
                }
                if (++count < declTypes.size()) {
                    dest.append(", ");
                }
            }
        } else {
            for (ValueType vt: typeParams) {
                vt.doPrettyPrint(dest, indent, ctx);
                if (++count < typeParams.size()) {
                    dest.append(", ");
                }
            }
        }
        dest.append(']');
    }
    @Override
    public Value getMetadata(TypeContext ctx) {
        return base.getMetadata(ctx);
    }

    @Override
    public boolean isTagged(TypeContext ctx) {
        return base.isTagged(ctx);
    }
}
