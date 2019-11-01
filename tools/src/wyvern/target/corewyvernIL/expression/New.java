package wyvern.target.corewyvernIL.expression;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import wyvern.stdlib.support.backend.BytecodeOuterClass;
import wyvern.target.corewyvernIL.BindingSite;
import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.decl.Declaration;
import wyvern.target.corewyvernIL.decl.ForwardDeclaration;
import wyvern.target.corewyvernIL.decl.NamedDeclaration;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.effects.EffectAccumulator;
import wyvern.target.corewyvernIL.support.EvalContext;
import wyvern.target.corewyvernIL.support.FailureReason;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.type.StructuralType;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.ToolError;

public class New extends Expression {
    private List<? extends Declaration> decls;
    private BindingSite selfSite;
    private boolean hasForward;
    private boolean moduleFlag = false;
    private ForwardDeclaration forwardDeclaration;

    /** convenience method for a single declaration */
    public New(NamedDeclaration decl, FileLocation loc) {
        this(Arrays.asList(decl), loc);
    }
    public New(NamedDeclaration decl) {
        this(decl, decl.getLocation());
    }

    /** convenience method for two declarations */
    public New(NamedDeclaration decl1, NamedDeclaration decl2) {
        this(Arrays.asList(decl1, decl2), decl1.getLocation());
    }

    /** computes the type itself, uses a don't care selfName */
    public New(List<NamedDeclaration> decls, FileLocation loc) {
        this(decls, new BindingSite("dontcare"), typeOf(decls), loc);
    }

    /*public New(List<? extends Declaration> decls, String selfName, ValueType type, FileLocation loc) {
        this(decls, new BindingSite(selfName), type, loc);
    }*/
    public New(List<? extends Declaration> decls, BindingSite selfSite, ValueType type, FileLocation loc) {
        super(type, loc);
        this.decls = decls;
        this.selfSite = selfSite;
        for (Declaration d : decls) {
            if (d == null) {
                throw new NullPointerException();
            }
        }

        Optional<? extends Declaration> forwardOption = decls.stream().filter(d -> d instanceof ForwardDeclaration).findFirst();

        hasForward = forwardOption.isPresent();
        if (hasForward) {
            forwardDeclaration = (ForwardDeclaration) forwardOption.get();
        }
    }

    @SuppressWarnings("unchecked")
    public List<Declaration> getDecls() {
        return (List<Declaration>) decls;
    }

    public String getSelfName() {
        return selfSite.getName();
    }

    public BindingSite getSelfSite() {
        return selfSite;
    }

    @Override
    public void doPrettyPrint(Appendable dest, String indent) throws IOException {
        dest.append("new ").append(getSelfName()).append(" : ");
        getType().doPrettyPrint(dest, indent + "  ");
        dest.append(" =>\n");

        for (Declaration decl: decls) {
            decl.doPrettyPrint(dest, indent + "    ");
        }
    }

    @Override
    public BytecodeOuterClass.Expression emitBytecode() {
        BytecodeOuterClass.Type type = getType().emitBytecodeType();
        String selfName = getSelfName();

        BytecodeOuterClass.Expression.NewExpression.Builder ne = BytecodeOuterClass.Expression.NewExpression.newBuilder()
                .setSelfName(selfName)
                .setType(type);

        for (Declaration d : decls) {
            ne.addDeclarations(d.emitBytecode());
        }
        return BytecodeOuterClass.Expression.newBuilder().setNewExpression(ne).build();
    }

    /** Returns a declaration of the proper name, or null if not found.
     * Searches separately for types/effects and values, since these are in different namespaces. */
    public Declaration findDecl(String name, boolean isTypeOrEffect) {
        for (Declaration d : decls) {
            if (name.equals(d.getName()) && isTypeOrEffect == d.isTypeOrEffectDecl()) {
                return d;
            }
        }
        return null;
    }

    @Override
    public <S, T> T acceptVisitor(ASTVisitor<S, T> emitILVisitor, S state) {
        return emitILVisitor.visit(state, this);
    }

    @Override
    public ValueType typeCheck(TypeContext ctx, EffectAccumulator effectAccumulator) {
        List<DeclType> dts = new LinkedList<DeclType>();

        TypeContext thisCtx = ctx.extend(selfSite, getType());

        boolean isResource = false;
        for (Declaration d : declsExceptForward()) {
            DeclType dt = d.typeCheck(ctx, thisCtx);
            dts.add(dt);
            if (d.containsResource(thisCtx) || dt.containsResource(thisCtx)) {
                isResource = true;
            }
        }

        ValueType type = getType();
        if (hasForward) {
            ValueType forwardObjectType = ctx.lookupTypeOf(forwardDeclaration.getFieldName());
            StructuralType forwardStructuralType = forwardObjectType.getStructuralType(thisCtx);
            // new defined declaration will override delegate object's method definition if they had subType relationship
            for (DeclType declType : forwardStructuralType.getDeclTypes()) {
                if (!dts.stream().anyMatch(newDefDeclType -> newDefDeclType.isSubtypeOf(declType, thisCtx, new FailureReason()))) {
                    dts.add(declType);
                }
            }
        }

        // check that everything in the claimed structural type was accounted for
        StructuralType requiredT = type.getStructuralType(ctx);
        StructuralType actualT = new StructuralType(selfSite, dts);
        actualT.checkForDuplicates();
        FailureReason r = new FailureReason();
        if (!actualT.isSubtypeOf(requiredT, ctx, r)) {
            // we disable warnings when there is a failure inside a SeqExpr
            // the issue is that Wyvern's type theory doesn't have singleton types,
            // and sometimes fails to reason about an object in a field of this new being the
            // same as a let-bound object in an earlier part of the module.
            if (!moduleFlag) {
                ToolError.reportError(ErrorMessage.NOT_SUBTYPE, this, actualT.desugar(ctx), requiredT.desugar(ctx), r.getReason());
            }
        }

        if (isResource && !requiredT.isResource(GenContext.empty())) {
            if (type instanceof StructuralType) {
                type = new StructuralType(selfSite, dts, isResource);
                this.setExprType(type);
            } else {
                // can't update the type
                ToolError.reportError(ErrorMessage.MUST_BE_ASSIGNED_TO_RESOURCE_TYPE, this);
            }
        }
        type.checkWellFormed(ctx);
        type.canInstantiate(ctx);

        return type;
    }

    @Override
    public Value interpret(EvalContext ctx) {
        Value result = null;

        // evaluate all decls
        List<Declaration> ds = new LinkedList<Declaration>();
        for (Declaration d : declsExceptForward()) {
        Declaration newD = d.interpret(ctx);
        ds.add(newD);
        }
        result = new ObjectValue(ds, selfSite, getType().interpret(ctx), forwardDeclaration, getLocation(), ctx);

        return result;
    }

    private List<Declaration> declsExceptForward() {
        return decls.stream().filter(x -> x != forwardDeclaration).collect(Collectors.toList());
    }

    @Override
    public Set<String> getFreeVariables() {
        Set<String> freeVars = new HashSet<>();
        if (hasForward) {
            freeVars.addAll(forwardDeclaration.getFreeVariables());
        }
        for (Declaration decl : decls) {
            freeVars.addAll(decl.getFreeVariables());
        }
        freeVars.remove(getSelfName());
        return freeVars;
    }
    
    public void setModuleFlag() {
        moduleFlag = true;
    }

    private static ValueType typeOf(List<NamedDeclaration> decls2) {
        List<DeclType> declts = new LinkedList<DeclType>();
        for (NamedDeclaration d : decls2) {
            declts.add(d.getDeclType());
        }
        ValueType type = new StructuralType("dontcare", declts);
        return type;
    }
}
