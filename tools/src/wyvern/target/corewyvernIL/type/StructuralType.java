package wyvern.target.corewyvernIL.type;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

import wyvern.target.corewyvernIL.FormalArg;
import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.decltype.DefDeclType;
import wyvern.target.corewyvernIL.decltype.VarDeclType;
import wyvern.target.corewyvernIL.expression.Variable;
import wyvern.target.corewyvernIL.support.EvalContext;
import wyvern.target.corewyvernIL.support.FailureReason;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.support.ReceiverView;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.support.View;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.HasLocation;
import wyvern.tools.errors.ToolError;
import wyvern.tools.typedAST.core.expressions.Fn;

public class StructuralType extends ValueType {
    private String selfName;
    private List<DeclType> declTypes;
    private boolean resourceFlag = false;

    public StructuralType(String selfName, List<DeclType> declTypes) {
        this(selfName, declTypes, false);
    }

    public StructuralType(String selfName, List<DeclType> declTypes, boolean resourceFlag) {
        super();
        this.selfName = selfName;
        // check a sanity condition
        //        if (declTypes != null && declTypes.size()>0)
        //            if (declTypes.get(0) == null)
        //                throw new NullPointerException("invariant: decl types should not be null");
        this.declTypes = declTypes;
        this.setResourceFlag(resourceFlag);
        // if there is a var declaration, it's a resource type
        for (DeclType dt : declTypes) {
            if (dt instanceof VarDeclType) {
                this.setResourceFlag(true);
            }
        }
    }

    private static StructuralType emptyType = new StructuralType("IGNORE_ME", Collections.emptyList());

    public static StructuralType getEmptyType() {
        return emptyType;
    }

    @Override
    public boolean isResource(TypeContext ctx) {
        return this.resourceFlag;
    }

    private void setResourceFlag(boolean isResource) {
        this.resourceFlag = isResource;
    }

    @Override
    public void doPrettyPrint(Appendable dest, String indent, TypeContext ctx) throws IOException {
        if (selfName.equals(Fn.LAMBDA_STRUCTUAL_DECL)) {
            DefDeclType ddt = (DefDeclType) getDeclTypes().get(0);
            boolean first = true;
            for (FormalArg arg: ddt.getFormalArgs()) {
                if (first) {
                    first = false;
                } else {
                    dest.append(" * ");
                }
                arg.getType().doPrettyPrint(dest, indent, ctx);
            }
            if (isResource(GenContext.empty())) {
                dest.append(" -> ");
            } else {
                dest.append(" => ");
            }
            ddt.getRawResultType().doPrettyPrint(dest, indent, ctx);
        } else {
            String newIndent = indent + "  ";
            if (isResource(GenContext.empty())) {
                dest.append("resource ");
            }
            dest.append("type { ");
            if (indent.length() == 0) {
                dest.append(selfName).append(" =>\n");
                for (DeclType dt : getDeclTypes()) {
                    dt.doPrettyPrint(dest, newIndent);
                }
                dest.append(indent).append(" }");
            } else {
                /** If we are already indented, then abbreviate. */
                dest.append("... }");
            }
        }
    }

    public String getSelfName() {
        return selfName;
    }

    public List<DeclType> getDeclTypes() {
        return declTypes;
    }

    public void setDeclTypes(List<DeclType> declTypes) {
        this.declTypes = declTypes;
    }

    @Override
    public <S, T> T acceptVisitor(ASTVisitor<S, T> emitILVisitor, S state) {
        return emitILVisitor.visit(state, this);
    }

    @Override
    public StructuralType getStructuralType(TypeContext ctx, StructuralType theDefault) {
        return this;
    }

    @Override
    public boolean isSubtypeOf(ValueType t, TypeContext ctx, FailureReason reason) {
        t = t.getCanonicalType(ctx);
        if (t instanceof DynamicType) {
            return true;
        }
        if (t instanceof NominalType) {
            StructuralType st = ((NominalType) t).getStructuralType(ctx, null);
            if (st == null) {
                if (!reason.isDefined()) {
                    reason.setReason("cannot look up structural type for " + t.desugar(ctx));
                }
                return false;
            } else {
                return isSubtypeOf(st, ctx, reason);
            }
        }

        if (t instanceof BottomType) {
            return false;
        }

        if (!(t instanceof StructuralType)) {
            reason.setReason("cannot look up structural type for " + t.desugar(ctx));
            return false;
        }

        StructuralType st = (StructuralType) t;
        st = (StructuralType) st.adapt(new ReceiverView(new Variable(st.selfName), new Variable(selfName)));

        TypeContext extendedCtx = ctx.extend(selfName, this);
        for (DeclType dt : st.getDeclTypes()) {
            DeclType candidateDT = findMatchingDecl(dt.getName(), cdt -> cdt.isTypeDecl() != dt.isTypeDecl(), ctx);
            //DeclType candidateDT = findDecl(dt.getName(), ctx);
            if (candidateDT == null) {
                if (!reason.isDefined()) {
                    reason.setReason("missing declaration " + dt.getName());
                }
                return false;
            } else if (!candidateDT.isSubtypeOf(dt, extendedCtx, reason)) {
                if (!reason.isDefined()) {
                    reason.setReason("declaration " + dt.getName() + " is not a subtype of the expected declaration");
                }
                return false;
            }
        }

        // a resource type is not a subtype of a non-resource type
        if (isResource(GenContext.empty()) && !st.isResource(GenContext.empty())) {
            reason.setReason("the second type is not a resource");
            return false;
        }

        return true;
    }

    /** Search for decl types with the name given.  Take out those that match the exclusionFilter.
     * If exactly one remains, return it.  Otherwise, return null.
     */
    DeclType findMatchingDecl(String name, Predicate<? super DeclType> exclusionFilter, TypeContext ctx) {
        List<DeclType> ds = findDecls(name, ctx);
        ds.removeIf(exclusionFilter);
        if (ds.size() != 1) {
            return null;
        } else {
            return ds.get(0);
        }
    }

    @Override
    public DeclType findDecl(String declName, TypeContext ctx) {
        DeclType theDecl = null;
        for (DeclType mdt : getDeclTypes()) {
            if (mdt.getName().equals(declName)) {
                if (theDecl != null) {
                    throw new RuntimeException("Ambiguous findDecl: " + declName);
                }
                theDecl = mdt;
            }
        }
        return theDecl;
    }

    @Override
    public List<DeclType> findDecls(String declName, TypeContext ctx) {
        List<DeclType> mdts = new LinkedList<DeclType>();
        for (DeclType mdt : getDeclTypes()) {
            if (mdt.getName().equals(declName)) {
                mdts.add(mdt);
            }
        }
        return mdts;
    }

    @Override
    public ValueType adapt(View v) {
        List<DeclType> newDTs = new LinkedList<DeclType>();
        for (DeclType dt : getDeclTypes()) {
            newDTs.add(dt.adapt(v));
        }
        return new StructuralType(selfName, newDTs, isResource(GenContext.empty()));
    }

    @Override
    public ValueType interpret(EvalContext ctx) {
        List<DeclType> newDTs = new LinkedList<DeclType>();
        for (DeclType dt : getDeclTypes()) {
            newDTs.add(dt.interpret(ctx));
        }
        return new StructuralType(selfName, newDTs, isResource(ctx));
    }

    @Override
    public void checkWellFormed(TypeContext ctx) {
        final TypeContext selfCtx = ctx.extend(selfName, this);
        for (DeclType dt : declTypes) {
            dt.checkWellFormed(selfCtx);
        }
    }

    @Override
    public ValueType doAvoid(String varName, TypeContext ctx, int count) {
        if (count > MAX_RECURSION_DEPTH) {
            ToolError.reportError(ErrorMessage.CANNOT_AVOID_VARIABLE, (HasLocation) null, varName);
        }
        if (varName.equals(selfName)) {
            return this;
        }
        List<DeclType> newDeclTypes = new LinkedList<DeclType>();
        boolean changed = false;
        for (DeclType dt : declTypes) {
            DeclType newDT = dt.doAvoid(varName, ctx, count + 1);
            newDeclTypes.add(newDT);
            if (newDT != dt) {
                changed = true;
            }
        }
        if (!changed) {
            return this;
        } else {
            return new StructuralType(selfName, newDeclTypes, resourceFlag);
        }
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[] {selfName, resourceFlag, declTypes});
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof StructuralType)) {
            return false;
        }
        StructuralType other = (StructuralType) obj;
        return resourceFlag == other.resourceFlag && selfName.equals(other.selfName) && declTypes.equals(other.declTypes);
    }

    @Override
    public boolean isTagged(TypeContext ctx) {
        return false;
    }


    /*@Override
    public String toString() {
        String ret = (resourceFlag?"[resource ":"[") + selfName + " => ";
        for(DeclType declt : declTypes) {
            ret += declt.toString();
            ret += ";";
        }
        return ret + "]";
    }*/
}
