package wyvern.target.corewyvernIL.type;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import wyvern.stdlib.support.backend.BytecodeOuterClass;
import wyvern.target.corewyvernIL.BindingSite;
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
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.HasLocation;
import wyvern.tools.errors.ToolError;
import wyvern.tools.typedAST.core.expressions.Fn;

public class StructuralType extends ValueType {
    private BindingSite selfSite;
    private List<DeclType> declTypes;
    private boolean resourceFlag = false;

    public StructuralType(String selfName, List<DeclType> declTypes) {
        this(selfName, declTypes, false);
    }

    public StructuralType(String selfName, List<DeclType> declTypes, boolean resourceFlag) {
        this(new BindingSite(selfName), declTypes, resourceFlag);
    }
    public StructuralType(BindingSite selfSite, List<DeclType> declTypes) {
        this(selfSite, declTypes, false);
    }
    public StructuralType(BindingSite selfSite, List<DeclType> declTypes, boolean resourceFlag) {
        this(selfSite, declTypes, resourceFlag, null);
    }
    public StructuralType(BindingSite selfSite, List<DeclType> declTypes, boolean resourceFlag, FileLocation loc) {
        super(loc);
        this.selfSite = selfSite;
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

    private static final StructuralType emptyType = new StructuralType("IGNORE_ME", Collections.emptyList());
    
    public void checkForDuplicates() {
        Set<String> valueNames = new HashSet<String>();  
        Set<String> typeNames = new HashSet<String>();  
        for (DeclType dt : declTypes) {
            Set<String> names = dt.isTypeOrEffectDecl() ? typeNames : valueNames;
            if (names.contains(dt.getName())) {
                ToolError.reportError(ErrorMessage.DUPLICATE_MEMBER, this, "Type", dt.getName());
            } else {
                names.add(dt.getName());
            }
        }
    }

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
        if (getSelfName().equals(Fn.LAMBDA_STRUCTUAL_DECL)) {
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
                dest.append(getSelfName()).append(" =>\n");
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

    @Override
    public BytecodeOuterClass.Type emitBytecodeType() {
        // replace with Dyn to dramatically shrink the generated bytecode
        return BytecodeOuterClass.Type.newBuilder().setSimpleType(BytecodeOuterClass.Type.SimpleType.Dyn).build();
        
        // original code outputs structural types - but creates a ton of duplication in bytecode
        /*BytecodeOuterClass.Type.Builder topType = BytecodeOuterClass.Type.newBuilder().setSimpleType(BytecodeOuterClass.Type.SimpleType.Top);
        BytecodeOuterClass.Type.CompoundType.Builder ct = BytecodeOuterClass.Type.CompoundType.newBuilder().setBase(topType).setSelfName(selfSite.getName())
                .setStateful(resourceFlag);
        for (DeclType d : declTypes) {
            ct.addDeclTypes(d.emitBytecode());
        }
        return BytecodeOuterClass.Type.newBuilder().setCompoundType(ct).build();*/
    }

    public String getSelfName() {
        return selfSite.getName();
    }

    public BindingSite getSelfSite() {
        return selfSite;
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
            if (t.isTagged(ctx)) {
                return false;
            }
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
            if (!reason.isDefined()) {
                reason.setReason("cannot look up structural type for " + t.desugar(ctx));
            }
            return false;
        }

        StructuralType st = (StructuralType) t;
        st = (StructuralType) st.adapt(new ReceiverView(new Variable(st.selfSite), new Variable(selfSite)));

        TypeContext extendedCtx = ctx.extend(selfSite, this);
        for (DeclType dt : st.getDeclTypes()) {
            // get all decls
            List<DeclType> candidates = findDecls(dt.getName(), ctx);
            //DeclType candidateDT = findMatchingDecl(dt.getName(), cdt -> cdt.isTypeOrEffectDecl() != dt.isTypeOrEffectDecl(), ctx);
            //DeclType candidateDT = findDecl(dt.getName(), ctx);
            if (candidates.isEmpty()) {
                if (!reason.isDefined()) {
                    reason.setReason("missing declaration " + dt.getName());
                }
                return false;
            }
            // filter ones that don't match
            candidates.removeIf(c -> !c.isSubtypeOf(dt, extendedCtx, reason));
            // if empty, error
            if (candidates.isEmpty()) {
                if (!reason.isDefined()) {
                    reason.setReason("declaration " + dt.getName() + " is not a subtype of the expected declaration");
                }
                return false;
            }
        }

        // a resource type is not a subtype of a non-resource type
        if (isResource(GenContext.empty()) && !st.isResource(GenContext.empty())) {
            if (!reason.isDefined()) {
                reason.setReason("the second type is not a resource");
            }
            return false;
        }

        return true;
    }

    /** Search for decl types with the name given.  Take out those that match the exclusionFilter.
     * If exactly one remains, return it.  Otherwise, return null.
     */
    @Override
    public DeclType findMatchingDecl(String name, Predicate<? super DeclType> exclusionFilter, TypeContext ctx) {
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
        return new StructuralType(selfSite, newDTs, isResource(GenContext.empty()));
    }

    @Override
    public ValueType interpret(EvalContext ctx) {
        List<DeclType> newDTs = new LinkedList<DeclType>();
        for (DeclType dt : getDeclTypes()) {
            newDTs.add(dt.interpret(ctx));
        }
        return new StructuralType(selfSite, newDTs, isResource(ctx));
    }

    @Override
    public void checkWellFormed(TypeContext ctx) {
        boolean needsResource = false;
        final TypeContext selfCtx = ctx.extend(selfSite, this);
        for (DeclType dt : declTypes) {
            dt.checkWellFormed(selfCtx);
            needsResource = needsResource || dt.containsResource(selfCtx);
        }
        if (needsResource && !resourceFlag) {
            ToolError.reportError(ErrorMessage.MUST_BE_A_RESOURCE, this, "This type");
        }
    }

    @Override
    public ValueType doAvoid(String varName, TypeContext ctx, int count) {
        if (count > MAX_RECURSION_DEPTH) {
            ToolError.reportError(ErrorMessage.CANNOT_AVOID_VARIABLE, (HasLocation) null, varName);
        }
        if (varName.equals(selfSite.getName())) {
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
            return new StructuralType(selfSite, newDeclTypes, resourceFlag);
        }
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[] {selfSite, resourceFlag, declTypes});
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof StructuralType)) {
            return false;
        }
        StructuralType other = (StructuralType) obj;
        return resourceFlag == other.resourceFlag && selfSite.equals(other.selfSite) && declTypes.equals(other.declTypes);
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

    @Override
    public boolean isEffectAnnotated(TypeContext ctx) {
        for (DeclType dt : this.getDeclTypes()) {
            if (!dt.isEffectAnnotated(ctx)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isEffectUnannotated(TypeContext ctx) {
        for (DeclType dt : this.getDeclTypes()) {
            if (!dt.isEffectUnannotated(ctx)) {
                return false;
            }
        }
        return true;
    }
}
