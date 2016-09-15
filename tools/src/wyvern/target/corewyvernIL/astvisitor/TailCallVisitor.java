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
import wyvern.target.corewyvernIL.expression.Cast;
import wyvern.target.corewyvernIL.expression.Expression;
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
import wyvern.target.corewyvernIL.metadata.IsTailRecursive;
import wyvern.target.corewyvernIL.support.EmptyTypeContext;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.support.Util;
import wyvern.target.corewyvernIL.type.CaseType;
import wyvern.target.corewyvernIL.type.DataType;
import wyvern.target.corewyvernIL.type.ExtensibleTagType;
import wyvern.target.corewyvernIL.type.NominalType;
import wyvern.target.corewyvernIL.type.StructuralType;
import wyvern.target.corewyvernIL.type.ValueType;

class State {
    public String method;
    public boolean inTailPosition;

    public State(String method, boolean inTailPosition) {
        this.method = method;
        this.inTailPosition = inTailPosition;
    }

    public State False() {
        return new State(method, false);
    }

    public State True() {
        return new State(method, true);
    }
}

/** TailCallVisitor finds all MethodCall expressions in a
 *  corewyvernIL AST and attaches an IsTailCall metadata to them
 *  if the method calls are tail calls.
 */
public class TailCallVisitor extends ASTVisitor<State, Boolean> {

    public static void annotate(IExpr program) {
        program.acceptVisitor(new TailCallVisitor(),
                              new State(null, false));
    }

    public Boolean visit(State state, New newExpr) {
        for (Declaration decl : newExpr.getDecls()) {
            decl.acceptVisitor(this, state);
        }
        return false;
    }

    public Boolean visit(State state, MethodCall methodCall) {
        methodCall.getObjectExpr().acceptVisitor(this,
                                                 state.False());

        boolean tailCall = state.inTailPosition &&
            (methodCall.getMethodName().equals(state.method));
        if (tailCall) {
            methodCall.addMetadata(new IsTailCall());
        }
        return tailCall;
    }


    public Boolean visit(State state, Match match) {
        match.getMatchExpr().acceptVisitor(this, state.False());
        Boolean tailRecursive =
            match.getElseExpr().acceptVisitor(this, state);
        for (Case matchCase : match.getCases()) {
            tailRecursive = tailRecursive ||
                (matchCase.getBody().acceptVisitor(this, state));
        }
        return tailRecursive;
    }


    public Boolean visit(State state, FieldGet fieldGet) {
        fieldGet.getObjectExpr().acceptVisitor(this, state.False());
        return false;
    }


    public Boolean visit(State state, Let let) {
        let.getToReplace().acceptVisitor(this, state.False());
        if (let.getVarName().equals(state.method))
            return false;
        else
            return let.getInExpr().acceptVisitor(this, state);
    }


    public Boolean visit(State state, FieldSet fieldSet) {
        fieldSet.getObjectExpr().acceptVisitor(this, state.False());
        fieldSet.getExprToAssign().acceptVisitor(this, state.False());
        return false;
    }


    public Boolean visit(State state, Variable variable) {
        return false;
    }


    public Boolean visit(State state, Cast cast) {
        cast.getToCastExpr().acceptVisitor(this, state.False());
        return false;
    }


    public Boolean visit(State state, VarDeclaration varDecl) {
        varDecl.getDefinition().acceptVisitor(this, state.False());
        return false;
    }

    public Boolean visit(State state, DefDeclaration defDecl) {
        if (defDecl.getBody().acceptVisitor(this,
                                            new State(defDecl.getName(),
                                                      true))) {
            defDecl.addMetadata(new IsTailRecursive());
        }
        return false;
    }

    public Boolean visit(State state, ValDeclaration valDecl) {
        valDecl.getDefinition().acceptVisitor(this, state.False());
        return false;
    }


    public Boolean visit(State state,
                         IntegerLiteral integerLiteral) {
        return false;
    }


    public Boolean visit(State state,
                         RationalLiteral rational) {
        return false;
    }


    public Boolean visit(State state,
                         FormalArg formalArg) {
        return false;
    }


    public Boolean visit(State state,
                         VarDeclType varDeclType) {
        return false;
    }

    public Boolean visit(State state,
                         ValDeclType valDeclType) {
        return false;
    }


    public Boolean visit(State state,
                         DefDeclType defDeclType) {
        return false;
    }


    public Boolean visit(State state,
                         AbstractTypeMember abstractDeclType) {
        return false;
    }

    public Boolean visit(State state,
                         StructuralType structuralType) {
        return false;
    }

    public Boolean visit(State state, NominalType nominalType) {
        return false;
    }

    public Boolean visit(State state, StringLiteral stringLiteral) {
        return false;
    }

    public Boolean visit(State state, DelegateDeclaration delegateDecl) {
        return false;
    }

    @Override
    public Boolean visit(State state, Bind bind) {
        for (IExpr expr : bind.getToReplaceExps()) {
            expr.acceptVisitor(this, state.False());
        }
        return bind.getInExpr().acceptVisitor(this, state);
    }

    @Override
    public Boolean visit(State state,
                         ConcreteTypeMember concreteTypeMember) {
        return false;
    }

    @Override
    public Boolean visit(State state,
                         TypeDeclaration typeDecl) {
        return false;
    }

    @Override
    public Boolean visit(State state,
                         CaseType caseType) {
        return false;
    }

    @Override
    public Boolean visit(State state,
                         ExtensibleTagType extensibleTagType) {
        return false;
    }

    @Override
    public Boolean visit(State state,
                         DataType dataType) {
        return false;
    }

    @Override
    public Boolean visit(State state, FFIImport ffiImport) {
        return false;
    }

}
