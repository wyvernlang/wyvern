package wyvern.target.corewyvernIL.astvisitor;

import wyvern.target.corewyvernIL.ASTNode;
import wyvern.target.corewyvernIL.Case;
import wyvern.target.corewyvernIL.Environment;
import wyvern.target.corewyvernIL.FormalArg;
import wyvern.target.corewyvernIL.binding.Binding;
import wyvern.target.corewyvernIL.decl.Declaration;
import wyvern.target.corewyvernIL.decl.DefDeclaration;
import wyvern.target.corewyvernIL.decl.DelegateDeclaration;
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
import wyvern.target.corewyvernIL.expression.Path;
import wyvern.target.corewyvernIL.expression.RationalLiteral;
import wyvern.target.corewyvernIL.expression.StringLiteral;
import wyvern.target.corewyvernIL.expression.Variable;
import wyvern.target.corewyvernIL.metadata.IsTailCall;
import wyvern.target.corewyvernIL.support.EmptyTypeContext;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.support.Util;
import wyvern.target.corewyvernIL.type.CaseType;
import wyvern.target.corewyvernIL.type.DataType;
import wyvern.target.corewyvernIL.type.ExtensibleTagType;
import wyvern.target.corewyvernIL.type.NominalType;
import wyvern.target.corewyvernIL.type.StructuralType;
import wyvern.target.corewyvernIL.type.ValueType;

/** TailCallVisitor finds all MethodCall expressions in a
 *  corewyvernIL AST and attaches an IsTailCall metadata to them
 *  if the method calls are tail calls.
 */
public class TailCallVisitor extends ASTVisitor<Boolean, Void> {

    public static void annotate(IExpr program) {
        program.acceptVisitor(new TailCallVisitor(), false);
    }

    public Void visit(Boolean inTailPosition, New newExpr) {
        for (Declaration decl : newExpr.getDecls()) {
            decl.acceptVisitor(this, false);
        }
        return null;
    }

    public Void visit(Boolean inTailPosition, Case c) {
        c.getBody().acceptVisitor(this, inTailPosition);
        return null;
    }

    public Void visit(Boolean inTailPosition, MethodCall methodCall) {
        methodCall.getObjectExpr().acceptVisitor(this, false);
        for (IExpr arg : methodCall.getArgs()) {
            arg.acceptVisitor(this, false);
        }

        if (inTailPosition) {
            methodCall.addMetadata(new IsTailCall());
        }
        return null;
    }


    public Void visit(Boolean inTailPosition, Match match) {
        match.getMatchExpr().acceptVisitor(this, false);
        match.getElseExpr().acceptVisitor(this, inTailPosition);
        for (Case matchCase : match.getCases()) {
            matchCase.getBody().acceptVisitor(this, inTailPosition);
        }
        return null;
    }


    public Void visit(Boolean inTailPosition, FieldGet fieldGet) {
        fieldGet.getObjectExpr().acceptVisitor(this, false);
        return null;
    }


    public Void visit(Boolean inTailPosition, Let let) {
        let.getToReplace().acceptVisitor(this, false);
        let.getInExpr().acceptVisitor(this, inTailPosition);
        return null;
    }


    public Void visit(Boolean inTailPosition, FieldSet fieldSet) {
        fieldSet.getObjectExpr().acceptVisitor(this, false);
        fieldSet.getExprToAssign().acceptVisitor(this, false);
        return null;
    }


    public Void visit(Boolean inTailPosition, Variable variable) {
        return null;
    }


    public Void visit(Boolean inTailPosition, Cast cast) {
        cast.getToCastExpr().acceptVisitor(this, false);
        return null;
    }


    public Void visit(Boolean inTailPosition, VarDeclaration varDecl) {
        varDecl.getDefinition().acceptVisitor(this, false);
        return null;
    }

    public Void visit(Boolean inTailPosition, DefDeclaration defDecl) {
        defDecl.getBody().acceptVisitor(this, true);
        return null;
    }

    public Void visit(Boolean inTailPosition, ValDeclaration valDecl) {
        valDecl.getDefinition().acceptVisitor(this, false);
        return null;
    }


    public Void visit(Boolean inTailPosition,
                         IntegerLiteral integerLiteral) {
        return null;
    }


    public Void visit(Boolean inTailPosition,
                      BooleanLiteral booleanLiteral) {
        return null;
    }


    public Void visit(Boolean inTailPosition,
                         RationalLiteral rational) {
        return null;
    }


    public Void visit(Boolean inTailPosition,
                         FormalArg formalArg) {
        return null;
    }


    public Void visit(Boolean inTailPosition,
                         VarDeclType varDeclType) {
        return null;
    }

    public Void visit(Boolean inTailPosition,
                         ValDeclType valDeclType) {
        return null;
    }


    public Void visit(Boolean inTailPosition,
                         DefDeclType defDeclType) {
        return null;
    }


    public Void visit(Boolean inTailPosition,
                         AbstractTypeMember abstractDeclType) {
        return null;
    }

    public Void visit(Boolean inTailPosition,
                         StructuralType structuralType) {
        return null;
    }

    public Void visit(Boolean inTailPosition, NominalType nominalType) {
        return null;
    }

    public Void visit(Boolean inTailPosition, StringLiteral stringLiteral) {
        return null;
    }

    public Void visit(Boolean inTailPosition, DelegateDeclaration delegateDecl) {
        return null;
    }

    @Override
    public Void visit(Boolean inTailPosition, Bind bind) {
        for (IExpr expr : bind.getToReplaceExps()) {
            expr.acceptVisitor(this, false);
        }
        bind.getInExpr().acceptVisitor(this, inTailPosition);
        return null;
    }

    @Override
    public Void visit(Boolean inTailPosition,
                         ConcreteTypeMember concreteTypeMember) {
        return null;
    }

    @Override
    public Void visit(Boolean inTailPosition,
                         TypeDeclaration typeDecl) {
        return null;
    }

    @Override
    public Void visit(Boolean inTailPosition,
                         CaseType caseType) {
        return null;
    }

    @Override
    public Void visit(Boolean inTailPosition,
                         ExtensibleTagType extensibleTagType) {
        return null;
    }

    @Override
    public Void visit(Boolean inTailPosition,
                         DataType dataType) {
        return null;
    }

    @Override
    public Void visit(Boolean inTailPosition, FFIImport ffiImport) {
        return null;
    }

    @Override
    public Void visit(Boolean inTailPosition, FFI ffi) {
        return null;
    }

}
