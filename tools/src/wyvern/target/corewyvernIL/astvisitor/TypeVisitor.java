package wyvern.target.corewyvernIL.astvisitor;

import wyvern.target.corewyvernIL.Case;
import wyvern.target.corewyvernIL.FormalArg;
import wyvern.target.corewyvernIL.decl.DefDeclaration;
import wyvern.target.corewyvernIL.decl.ForwardDeclaration;
import wyvern.target.corewyvernIL.decl.EffectDeclaration;
import wyvern.target.corewyvernIL.decl.ModuleDeclaration;
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
import wyvern.target.corewyvernIL.expression.FieldGet;
import wyvern.target.corewyvernIL.expression.FieldSet;
import wyvern.target.corewyvernIL.expression.FloatLiteral;
import wyvern.target.corewyvernIL.expression.IntegerLiteral;
import wyvern.target.corewyvernIL.expression.Let;
import wyvern.target.corewyvernIL.expression.Match;
import wyvern.target.corewyvernIL.expression.MethodCall;
import wyvern.target.corewyvernIL.expression.New;
import wyvern.target.corewyvernIL.expression.RationalLiteral;
import wyvern.target.corewyvernIL.expression.SeqExpr;
import wyvern.target.corewyvernIL.expression.StringLiteral;
import wyvern.target.corewyvernIL.expression.Variable;
import wyvern.tools.interop.FFI;
import wyvern.tools.interop.FFIImport;

public abstract class TypeVisitor<S, T> extends ASTVisitor<S, T> {
    private final String name;

    protected TypeVisitor(String name) {
        this.name = name;
    }

    private String errorMessage() {
        return this.name + " should only visit types";
    }

    @Override
    public T visit(S state, New newExpr) {
        throw new RuntimeException(this.errorMessage());
    }

    @Override
    public T visit(S state, Case c) {
        throw new RuntimeException(this.errorMessage());
    }

    @Override
    public T visit(S state, MethodCall methodCall) {
        throw new RuntimeException(this.errorMessage());
    }

    @Override
    public T visit(S state, Match match) {
        throw new RuntimeException(this.errorMessage());
    }

    @Override
    public T visit(S state, FieldGet fieldGet) {
        throw new RuntimeException(this.errorMessage());
    }

    @Override
    public T visit(S state, Let let) {
        throw new RuntimeException(this.errorMessage());
    }

    @Override
    public T visit(S state, Bind bind) {
        throw new RuntimeException(this.errorMessage());
    }

    @Override
    public T visit(S state, FieldSet fieldSet) {
        throw new RuntimeException(this.errorMessage());
    }

    @Override
    public T visit(S state, Variable variable) {
        throw new RuntimeException(this.errorMessage());
    }

    @Override
    public T visit(S state, Cast cast) {
        throw new RuntimeException(this.errorMessage());
    }

    @Override
    public T visit(S state, VarDeclaration varDecl) {
        throw new RuntimeException(this.errorMessage());
    }

    @Override
    public T visit(S state, DefDeclaration defDecl) {
        throw new RuntimeException(this.errorMessage());
    }

    @Override
    public T visit(S state, ValDeclaration valDecl) {
        throw new RuntimeException(this.errorMessage());
    }

    @Override
    public T visit(S state, ModuleDeclaration moduleDecl) {
        throw new RuntimeException(this.errorMessage());
    }

    @Override
    public T visit(S state, IntegerLiteral integerLiteral) {
        throw new RuntimeException(this.errorMessage());
    }

    @Override
    public T visit(S state, BooleanLiteral booleanLiteral) {
        throw new RuntimeException(this.errorMessage());
    }

    @Override
    public T visit(S state, RationalLiteral rational) {
        throw new RuntimeException(this.errorMessage());
    }

    @Override
    public T visit(S state, StringLiteral stringLiteral) {
        throw new RuntimeException(this.errorMessage());
    }

    @Override
    public T visit(S state, CharacterLiteral characterLiteral) {
        throw new RuntimeException(this.errorMessage());
    }

    @Override
    public T visit(S state, ForwardDeclaration forwardDecl) {
        throw new RuntimeException(this.errorMessage());
    }

    @Override
    public T visit(S state, TypeDeclaration typeDecl) {
        throw new RuntimeException(this.errorMessage());
    }

    @Override
    public T visit(S state, FFIImport ffiImport) {
        throw new RuntimeException(this.errorMessage());
    }

    @Override
    public T visit(S state, FFI ffi) {
        throw new RuntimeException(this.errorMessage());
    }

    @Override
    public T visit(S state, EffectDeclaration effectDeclaration) {
        throw new RuntimeException(this.errorMessage());
    }

    @Override
    public T visit(S state, SeqExpr seqExpr) {
        throw new RuntimeException(this.errorMessage());
    }

    @Override
    public T visit(S state, FloatLiteral flt) {
        throw new RuntimeException(this.errorMessage());
    }

    @Override
    public T visit(S state, ValDeclType valDeclType) {
        throw new RuntimeException(this.errorMessage());
    }

    @Override
    public T visit(S state, VarDeclType varDeclType) {
        throw new RuntimeException(this.errorMessage());
    }

    @Override
    public T visit(S state, DefDeclType defDeclType) {
        throw new RuntimeException(this.errorMessage());
    }

    @Override
    public T visit(S state, AbstractTypeMember abstractDeclType) {
        throw new RuntimeException(this.errorMessage());
    }

    @Override
    public T visit(S state, ConcreteTypeMember concreteTypeMember) {
        throw new RuntimeException(this.errorMessage());
    }

    @Override
    public T visit(S state, EffectDeclType effectDeclType) {
        throw new RuntimeException(this.errorMessage());
    }

    @Override
    public T visit(S state, FormalArg formalArg) {
        throw new RuntimeException(this.errorMessage());
    }
}
