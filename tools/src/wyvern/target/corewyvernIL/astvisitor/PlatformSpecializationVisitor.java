package wyvern.target.corewyvernIL.astvisitor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import wyvern.target.corewyvernIL.ASTNode;
import wyvern.target.corewyvernIL.Case;
import wyvern.target.corewyvernIL.FormalArg;
import wyvern.target.corewyvernIL.VarBinding;
import wyvern.target.corewyvernIL.decl.Declaration;
import wyvern.target.corewyvernIL.decl.DefDeclaration;
import wyvern.target.corewyvernIL.decl.ForwardDeclaration;
import wyvern.target.corewyvernIL.decl.EffectDeclaration;
import wyvern.target.corewyvernIL.decl.ModuleDeclaration;
import wyvern.target.corewyvernIL.decl.RecConstructDeclaration;
import wyvern.target.corewyvernIL.decl.RecDeclaration;
import wyvern.target.corewyvernIL.decl.TypeDeclaration;
import wyvern.target.corewyvernIL.decl.ValDeclaration;
import wyvern.target.corewyvernIL.decl.VarDeclaration;
import wyvern.target.corewyvernIL.decltype.AbstractTypeMember;
import wyvern.target.corewyvernIL.decltype.ConcreteTypeMember;
import wyvern.target.corewyvernIL.decltype.DefDeclType;
import wyvern.target.corewyvernIL.decltype.EffectDeclType;
import wyvern.target.corewyvernIL.decltype.ValDeclType;
import wyvern.target.corewyvernIL.decltype.VarDeclType;
import wyvern.target.corewyvernIL.expression.Bind;
import wyvern.target.corewyvernIL.expression.BooleanLiteral;
import wyvern.target.corewyvernIL.expression.Cast;
import wyvern.target.corewyvernIL.expression.CharacterLiteral;
import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.expression.FieldGet;
import wyvern.target.corewyvernIL.expression.FieldSet;
import wyvern.target.corewyvernIL.expression.FloatLiteral;
import wyvern.target.corewyvernIL.expression.IExpr;
import wyvern.target.corewyvernIL.expression.IntegerLiteral;
import wyvern.target.corewyvernIL.expression.Let;
import wyvern.target.corewyvernIL.expression.Match;
import wyvern.target.corewyvernIL.expression.MethodCall;
import wyvern.target.corewyvernIL.expression.New;
import wyvern.target.corewyvernIL.expression.RationalLiteral;
import wyvern.target.corewyvernIL.expression.SeqExpr;
import wyvern.target.corewyvernIL.expression.StringLiteral;
import wyvern.target.corewyvernIL.expression.Variable;
import wyvern.target.corewyvernIL.modules.TypedModuleSpec;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.support.InterpreterState;
import wyvern.target.corewyvernIL.support.ModuleResolver;
import wyvern.target.corewyvernIL.type.DataType;
import wyvern.target.corewyvernIL.type.ExtensibleTagType;
import wyvern.target.corewyvernIL.type.NominalType;
import wyvern.target.corewyvernIL.type.RefinementType;
import wyvern.target.corewyvernIL.type.StructuralType;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.HasLocation;
import wyvern.tools.interop.FFI;
import wyvern.tools.interop.FFIImport;
import wyvern.tools.util.Pair;

class PSVState {
    private String platform;
    private GenContext ctx;
    private HashSet<TypedModuleSpec> dependencies;

    PSVState(String platform, GenContext ctx) {
        this.setPlatform(platform);
        this.setCtx(ctx);
        this.setDependencies(new HashSet<>());
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public GenContext getCtx() {
        return ctx;
    }

    public void setCtx(GenContext ctx) {
        this.ctx = ctx;
    }

    public HashSet<TypedModuleSpec> getDependencies() {
        return dependencies;
    }

    public void setDependencies(HashSet<TypedModuleSpec> dependencies) {
        this.dependencies = dependencies;
    }
}

public class PlatformSpecializationVisitor extends ASTVisitor<PSVState, ASTNode> {

    /** Specializes all ModuleDeclarations in ast to the given platform.
     * Collects dependencies along the way and wraps the resulting expression with them as needed.
     * 
     * @param ast
     * @param platform
     * @param ctx
     * @return
     */
    public static ASTNode specializeAST(ASTNode ast, String platform, GenContext ctx) {
        PSVState state = new PSVState(platform, ctx);
        ASTNode result = ast.acceptVisitor(new PlatformSpecializationVisitor(), state);

        ModuleResolver interpResolver = ModuleResolver.getLocal();
        final ModuleResolver resolver = new ModuleResolver(platform, interpResolver.getRootDir(), interpResolver.getLibDir());
        resolver.setInterpreterState(InterpreterState.getLocalThreadInterpreter());


        List<TypedModuleSpec> dependenciesList = new ArrayList<>(state.getDependencies());
        dependenciesList = resolver.sortDependencies(dependenciesList);
        // TODO: may need to add dependencies if the resolved module depends on something else.
        // in that case, though, rather than blindly wrapping, should only wrap with new dependencies,
        // and may need to preserve order with existing dependencies.
        // question: might a dependency also need specialization?
        // perhaps we need a more global strategy that recursively adds dependencies to
        // the list (in order) and specializes each one as it is added?
        return (ASTNode) result; //resolver.wrap((IExpr) result, dependenciesList);
    }

    public ASTNode visit(PSVState state, ModuleDeclaration moduleDecl) {
        Pair<Declaration, List<TypedModuleSpec>> pair = moduleDecl.specialize(state.getPlatform(), state.getCtx());
        for (TypedModuleSpec spec : pair.getSecond()) {
            state.getDependencies().add(spec);
        }
        return pair.getFirst();
    }

    public ASTNode visit(PSVState state, New newExpr) {
        ArrayList<Declaration> newDecls = new ArrayList<>();
        for (Declaration decl : newExpr.getDecls()) {
            ASTNode result = decl.acceptVisitor(this, state);
            newDecls.add((Declaration) result);
        }
        New result = new New(newDecls, newExpr.getSelfSite(), newExpr.getType(), newExpr.getLocation());
        result.copyMetadata(newExpr);
        return result;
    }

    public ASTNode visit(PSVState state, Case c) {
        Expression resultBody = (Expression) c.getBody().acceptVisitor(this, state);
        Case result = new Case(c.getSite(), c.getPattern(), resultBody);
        result.copyMetadata(c);
        return result;
    }

    public ASTNode visit(PSVState state, MethodCall methodCall) {
        IExpr objExpr = (IExpr) methodCall.getObjectExpr().acceptVisitor(this, state);
        List<IExpr> args = new ArrayList<>();
        for (IExpr arg : methodCall.getArgs()) {
            args.add((IExpr) arg.acceptVisitor(this, state));
        }

        MethodCall result = new MethodCall(objExpr, methodCall.getMethodName(), args, methodCall);
        result.copyMetadata(methodCall);
        return result;
    }


    public ASTNode visit(PSVState state, Match match) {
        Expression matchExpr = (Expression) match.getMatchExpr().acceptVisitor(this, state);
        Expression elseExpr = match.getElseExpr();
        elseExpr =  elseExpr == null ? null : (Expression) elseExpr.acceptVisitor(this, state);
        List<Case> cases = new ArrayList<Case>();
        for (Case matchCase : match.getCases()) {
            cases.add((Case) matchCase.acceptVisitor(this, state));
        }

        Match result = new Match(matchExpr, elseExpr, cases, match.getType(), match.getLocation());
        result.copyMetadata(match);
        return result;
    }


    public ASTNode visit(PSVState state, FieldGet fieldGet) {
        IExpr resultExpr = (IExpr) fieldGet.getObjectExpr().acceptVisitor(this, state);
        FieldGet result = new FieldGet(resultExpr, fieldGet.getName(), fieldGet.getLocation());
        result.copyMetadata(fieldGet);
        return result;
    }


    public ASTNode visit(PSVState state, Let let) {
        IExpr toReplace = (IExpr) let.getToReplace().acceptVisitor(this, state);
        IExpr inExpr = (IExpr) let.getInExpr().acceptVisitor(this, state);

        Let result = new Let(let.getSite(), let.getVarType(), toReplace, inExpr);
        result.copyMetadata(let);
        return result;
    }


    public ASTNode visit(PSVState state, FieldSet fieldSet) {
        IExpr objectExpr = (IExpr) fieldSet.getObjectExpr().acceptVisitor(this, state);
        IExpr exprToAssign = (IExpr) fieldSet.getExprToAssign().acceptVisitor(this, state);

        FieldSet result = new FieldSet(fieldSet.getType(), objectExpr, fieldSet.getFieldName(), exprToAssign);
        result.copyMetadata(fieldSet);
        return result;
    }


    public ASTNode visit(PSVState state, Variable variable) {
        return variable;
    }


    public ASTNode visit(PSVState state, Cast cast) {
        IExpr resultExpr = (IExpr) cast.getToCastExpr().acceptVisitor(this, state);
        Cast result = new Cast(resultExpr, cast.getType());
        result.copyMetadata(cast);
        return result;
    }


    public ASTNode visit(PSVState state, VarDeclaration varDecl) {
        ASTNode resultDefinition = (ASTNode) varDecl.getDefinition().acceptVisitor(this, state);
        VarDeclaration result = new VarDeclaration(varDecl.getName(), varDecl.getType(), (IExpr) resultDefinition, varDecl.getLocation());
        result.copyMetadata(varDecl);
        return result;
    }

    public ASTNode visit(PSVState state, DefDeclaration defDecl) {
        ASTNode resultBody = (ASTNode) defDecl.getBody().acceptVisitor(this, state);
        DefDeclaration result = new DefDeclaration(
                defDecl.getName(),
                defDecl.getFormalArgs(),
                defDecl.getType(),
                (IExpr) resultBody,
                defDecl.getLocation(),
                defDecl.getEffectSet()
        );
        result.copyMetadata(defDecl);
        return result;
    }

    public ASTNode visit(PSVState state, ValDeclaration valDecl) {
        ASTNode resultDefinition = (ASTNode) valDecl.getDefinition().acceptVisitor(this, state);
        ValDeclaration result = new ValDeclaration(valDecl.getName(), valDecl.getType(), (IExpr) resultDefinition, valDecl.getLocation());
        result.copyMetadata(valDecl);
        return result;
    }

    public ASTNode visit(PSVState state,
            IntegerLiteral integerLiteral) {
        return integerLiteral;
    }


    public ASTNode visit(PSVState state,
            BooleanLiteral booleanLiteral) {
        return booleanLiteral;
    }


    public ASTNode visit(PSVState state,
            RationalLiteral rational) {
        return rational;
    }


    public ASTNode visit(PSVState state,
            FormalArg formalArg) {
        return formalArg;
    }


    public ASTNode visit(PSVState state,
            VarDeclType varDeclType) {
        return varDeclType;
    }

    public ASTNode visit(PSVState state,
            ValDeclType valDeclType) {
        return valDeclType;
    }


    public ASTNode visit(PSVState state,
            DefDeclType defDeclType) {
        return defDeclType;
    }


    public ASTNode visit(PSVState state,
            AbstractTypeMember abstractDeclType) {
        return abstractDeclType;
    }

    public ASTNode visit(PSVState state,
            StructuralType structuralType) {
        return structuralType;
    }

    public ASTNode visit(PSVState state, NominalType nominalType) {
        return nominalType;
    }

    public ASTNode visit(PSVState state, StringLiteral stringLiteral) {
        return stringLiteral;
    }
    
    public ASTNode visit(PSVState state, CharacterLiteral characterLiteral) {
        return characterLiteral;
    }

    public ASTNode visit(PSVState state, ForwardDeclaration forwardDecl) {
        return forwardDecl;
    }

    @Override
    public ASTNode visit(PSVState state, Bind bind) {
        List<VarBinding> bindings = new ArrayList<>();
        for (VarBinding binding : bind.getBindings()) {
            bindings.add(new VarBinding(binding.getVarName(),
                    binding.getType(),
                    (IExpr) binding.getExpression().acceptVisitor(this, state)));
        }
        IExpr inExpr = (IExpr) bind.getInExpr().acceptVisitor(this, state);

        Bind result = new Bind(bindings, inExpr);
        result.copyMetadata(bind);
        return result;
    }

    @Override
    public ASTNode visit(PSVState state,
            ConcreteTypeMember concreteTypeMember) {
        return concreteTypeMember;
    }

    @Override
    public ASTNode visit(PSVState state,
            TypeDeclaration typeDecl) {
        return typeDecl;
    }

    @Override
    public ASTNode visit(PSVState state,
            ValueType caseType) {
        return caseType;
    }

    @Override
    public ASTNode visit(PSVState state,
            ExtensibleTagType extensibleTagType) {
        return extensibleTagType;
    }

    @Override
    public ASTNode visit(PSVState state,
            DataType dataType) {
        return dataType;
    }

    @Override
    public ASTNode visit(PSVState state, FFIImport ffiImport) {
        return ffiImport;
    }

    @Override
    public ASTNode visit(PSVState state, FFI ffi) {
        return ffi;
    }

    @Override
    public ASTNode visit(PSVState state, EffectDeclaration effectDeclaration) { // unsure
        return effectDeclaration;
    }

    @Override
    public ASTNode visit(PSVState state, SeqExpr seqExpr) {
        SeqExpr newExpr = new SeqExpr();
        for (HasLocation hl : seqExpr.getElements()) {
            if (hl instanceof IExpr) {
                newExpr.addExpr((IExpr) ((IExpr) hl).acceptVisitor(this, state));
            } else {
                VarBinding vb = (VarBinding) hl;
                IExpr expr = (IExpr) vb.getExpression().acceptVisitor(this, state);
                newExpr.addBindingLast(new VarBinding(vb.getSite(), vb.getType(), expr));
            }
        }

        newExpr.copyMetadata(seqExpr);
        return newExpr;
    }

    @Override
    public ASTNode visit(PSVState state, EffectDeclType effectDeclType) {
        return effectDeclType;
    }

    @Override
    public ASTNode visit(PSVState state, RefinementType type) {
        return type;
    }

    public ASTNode visit(PSVState state, FloatLiteral flt)  {
      return flt;
    }

  @Override
  public ASTNode visit(PSVState state, RecDeclaration recDecl) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public ASTNode visit(PSVState state, RecConstructDeclaration recConstructDecl) {
    // TODO Auto-generated method stub
    return null;
  }
}
