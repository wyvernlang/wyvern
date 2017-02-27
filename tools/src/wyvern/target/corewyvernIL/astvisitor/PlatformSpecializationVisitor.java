package wyvern.target.corewyvernIL.astvisitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import wyvern.target.corewyvernIL.ASTNode;
import wyvern.target.corewyvernIL.Case;
import wyvern.target.corewyvernIL.FormalArg;
import wyvern.target.corewyvernIL.VarBinding;
import wyvern.target.corewyvernIL.decl.Declaration;
import wyvern.target.corewyvernIL.decl.DefDeclaration;
import wyvern.target.corewyvernIL.decl.DelegateDeclaration;
import wyvern.target.corewyvernIL.decl.ModuleDeclaration;
import wyvern.target.corewyvernIL.decl.TypeDeclaration;
import wyvern.target.corewyvernIL.decl.ValDeclaration;
import wyvern.target.corewyvernIL.decl.VarDeclaration;
import wyvern.target.corewyvernIL.decltype.AbstractTypeMember;
import wyvern.target.corewyvernIL.decltype.ConcreteTypeMember;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.decltype.DefDeclType;
import wyvern.target.corewyvernIL.decltype.ValDeclType;
import wyvern.target.corewyvernIL.decltype.VarDeclType;
import wyvern.target.corewyvernIL.expression.Bind;
import wyvern.target.corewyvernIL.expression.BooleanLiteral;
import wyvern.target.corewyvernIL.expression.Cast;
import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.expression.FFI;
import wyvern.target.corewyvernIL.expression.FFIImport;
import wyvern.target.corewyvernIL.expression.FieldGet;
import wyvern.target.corewyvernIL.expression.FieldSet;
import wyvern.target.corewyvernIL.expression.IExpr;
import wyvern.target.corewyvernIL.expression.IntegerLiteral;
import wyvern.target.corewyvernIL.expression.Let;
import wyvern.target.corewyvernIL.expression.Match;
import wyvern.target.corewyvernIL.expression.MethodCall;
import wyvern.target.corewyvernIL.expression.New;
import wyvern.target.corewyvernIL.expression.RationalLiteral;
import wyvern.target.corewyvernIL.expression.StringLiteral;
import wyvern.target.corewyvernIL.expression.Variable;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.type.DataType;
import wyvern.target.corewyvernIL.type.ExtensibleTagType;
import wyvern.target.corewyvernIL.type.NominalType;
import wyvern.target.corewyvernIL.type.StructuralType;
import wyvern.target.corewyvernIL.type.ValueType;

class PSVState {
    public String platform;
    public GenContext ctx;
    public PSVState(String platform, GenContext ctx) {
        this.platform = platform;
        this.ctx = ctx;
    }
}

public class PlatformSpecializationVisitor extends ASTVisitor<PSVState, ASTNode> {

    public static ASTNode specializeAST(ASTNode ast, String platform, GenContext ctx) {
        return ast.acceptVisitor(new PlatformSpecializationVisitor(), new PSVState(platform, ctx));
    }

    public ASTNode visit(PSVState state, ModuleDeclaration moduleDecl) {
        return moduleDecl.specialize(state.platform, state.ctx);
    }

    public ASTNode visit(PSVState state, New newExpr) {
        ArrayList<Declaration> newDecls = new ArrayList<>();
        for (Declaration decl : newExpr.getDecls()) {
            newDecls.add((Declaration)decl.acceptVisitor(this, state));
        }
        New result = new New(newDecls, newExpr.getSelfName(), newExpr.getExprType(), newExpr.getLocation());
        result.copyMetadata(newExpr);
        return result;
    }

    public ASTNode visit(PSVState state, Case c) {
        Case result = (Case)c.getBody().acceptVisitor(this, state);
        result.copyMetadata(c);
        return result;
    }

    public ASTNode visit(PSVState state, MethodCall methodCall) {
        IExpr objExpr = (IExpr)methodCall.getObjectExpr().acceptVisitor(this, state);
        List<IExpr> args = new ArrayList<>();
        for (IExpr arg : methodCall.getArgs()) {
            args.add((IExpr)arg.acceptVisitor(this, state));
        }

        MethodCall result = new MethodCall(objExpr, methodCall.getMethodName(), args, methodCall);
        result.copyMetadata(methodCall);
        return result;
    }


    public ASTNode visit(PSVState state, Match match) {
        Expression matchExpr = (Expression)match.getMatchExpr().acceptVisitor(this, state);
        Expression elseExpr = (Expression)match.getElseExpr().acceptVisitor(this, state);
        List<Case> cases = new ArrayList<Case>();
        for (Case matchCase : match.getCases()) {
            cases.add((Case)matchCase.getBody().acceptVisitor(this, state));
        }

        Match result = new Match(matchExpr, elseExpr, cases);
        result.copyMetadata(match);
        return result;
    }


    public ASTNode visit(PSVState state, FieldGet fieldGet) {
        FieldGet result = (FieldGet)fieldGet.getObjectExpr().acceptVisitor(this, state);
        result.copyMetadata(fieldGet);
        return result;
    }


    public ASTNode visit(PSVState state, Let let) {
        IExpr toReplace = (IExpr)let.getToReplace().acceptVisitor(this, state);
        IExpr inExpr = (IExpr)let.getInExpr().acceptVisitor(this, state);

        Let result = new Let(let.getVarName(), let.getExprType(), toReplace, inExpr);
        result.copyMetadata(let);
        return result;
    }


    public ASTNode visit(PSVState state, FieldSet fieldSet) {
        IExpr objectExpr = (IExpr)fieldSet.getObjectExpr().acceptVisitor(this, state);
        IExpr exprToAssign = (IExpr)fieldSet.getExprToAssign().acceptVisitor(this, state);

        FieldSet result = new FieldSet(fieldSet.getExprType(), objectExpr, fieldSet.getFieldName(), exprToAssign);
        result.copyMetadata(fieldSet);
        return result;
    }


    public ASTNode visit(PSVState state, Variable variable) {
        return variable;
    }


    public ASTNode visit(PSVState state, Cast cast) {
        Cast result = (Cast)cast.getToCastExpr().acceptVisitor(this, state);
        result.copyMetadata(cast);
        return result;
    }


    public ASTNode visit(PSVState state, VarDeclaration varDecl) {
        VarDeclaration result = (VarDeclaration)varDecl.getDefinition().acceptVisitor(this, state);
        result.copyMetadata(varDecl);
        return result;
    }

    public ASTNode visit(PSVState state, DefDeclaration defDecl) {
        ASTNode result = (ASTNode)defDecl.getBody().acceptVisitor(this, state);
        result.copyMetadata(defDecl);
        return result;
    }

    public ASTNode visit(PSVState state, ValDeclaration valDecl) {
        ValDeclaration result = (ValDeclaration)valDecl.getDefinition().acceptVisitor(this, state);
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

    public ASTNode visit(PSVState state, DelegateDeclaration delegateDecl) {
        return delegateDecl;
    }

    @Override
    public ASTNode visit(PSVState state, Bind bind) {
        List<VarBinding> bindings = new ArrayList<>();
        for (VarBinding binding : bind.getBindings()) {
            bindings.add(new VarBinding(binding.getVarName(),
                                        binding.getType(),
                                        (IExpr)binding.getExpression().acceptVisitor(this, state)));
        }
        IExpr inExpr = (IExpr)bind.getInExpr().acceptVisitor(this, state);

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
}
