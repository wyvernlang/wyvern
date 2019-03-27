package wyvern.tools.typedAST.typedastvisitor;

import wyvern.tools.parsing.DSLLit;
import wyvern.tools.typedAST.core.Script;
import wyvern.tools.typedAST.core.Sequence;
import wyvern.tools.typedAST.core.binding.NameBindingImpl;
import wyvern.tools.typedAST.core.declarations.DeclSequence;
import wyvern.tools.typedAST.core.declarations.DefDeclaration;
import wyvern.tools.typedAST.core.declarations.DelegateDeclaration;
import wyvern.tools.typedAST.core.declarations.EffectDeclaration;
import wyvern.tools.typedAST.core.declarations.ImportDeclaration;
import wyvern.tools.typedAST.core.declarations.Instantiation;
import wyvern.tools.typedAST.core.declarations.ModuleDeclaration;
import wyvern.tools.typedAST.core.declarations.TypeAbbrevDeclaration;
import wyvern.tools.typedAST.core.declarations.TypeDeclaration;
import wyvern.tools.typedAST.core.declarations.TypeVarDecl;
import wyvern.tools.typedAST.core.declarations.ValDeclaration;
import wyvern.tools.typedAST.core.declarations.VarDeclaration;
import wyvern.tools.typedAST.core.expressions.Application;
import wyvern.tools.typedAST.core.expressions.Assertion;
import wyvern.tools.typedAST.core.expressions.Assignment;
import wyvern.tools.typedAST.core.expressions.Case;
import wyvern.tools.typedAST.core.expressions.Fn;
import wyvern.tools.typedAST.core.expressions.Invocation;
import wyvern.tools.typedAST.core.expressions.LetExpr;
import wyvern.tools.typedAST.core.expressions.Match;
import wyvern.tools.typedAST.core.expressions.New;
import wyvern.tools.typedAST.core.expressions.TaggedInfo;
import wyvern.tools.typedAST.core.expressions.Variable;
import wyvern.tools.typedAST.core.values.BooleanConstant;
import wyvern.tools.typedAST.core.values.CharacterConstant;
import wyvern.tools.typedAST.core.values.FloatConstant;
import wyvern.tools.typedAST.core.values.IntegerConstant;
import wyvern.tools.typedAST.core.values.StringConstant;
import wyvern.tools.typedAST.core.values.UnitVal;
import wyvern.tools.typedAST.interfaces.TypedAST;

import java.util.Iterator;

public class EffectCheckVisitor extends TypedASTVisitor<EffectCheckState, Void> {

    @Override
    public Void visit(EffectCheckState state, NameBindingImpl ast) {
        return null;
    }

    @Override
    public Void visit(EffectCheckState state, DeclSequence ast) {
        Sequence normalSeq = ast.filterNormal();
        Iterator<TypedAST> astIterator = normalSeq.flatten();
        while (astIterator.hasNext()) {
            TypedAST nextAST = astIterator.next();
            nextAST.acceptVisitor(this, state);
        }
        return null;
    }

    @Override
    public Void visit(EffectCheckState state, DefDeclaration ast) {
        ast.getBody().acceptVisitor(this, state);
        return null;
    }

    @Override
    public Void visit(EffectCheckState state, DelegateDeclaration ast) {
        return null;
    }

    @Override
    public Void visit(EffectCheckState state, EffectDeclaration ast) {
        return null;
    }

    @Override
    public Void visit(EffectCheckState state, ImportDeclaration ast) {
        return null;
    }

    @Override
    public Void visit(EffectCheckState state, Instantiation ast) {
        for (TypedAST t : ast.getArgs()) {
            t.acceptVisitor(this, state);
        }
        return null;
    }

    @Override
    public Void visit(EffectCheckState state, ModuleDeclaration ast) {
        ast.getInner().acceptVisitor(this, state);
        return null;
    }

    @Override
    public Void visit(EffectCheckState state, TypeAbbrevDeclaration ast) {
        return null;
    }

    @Override
    public Void visit(EffectCheckState state, TypeDeclaration ast) {
        return null;
    }

    @Override
    public Void visit(EffectCheckState state, TypeVarDecl ast) {
        return null;
    }

    @Override
    public Void visit(EffectCheckState state, ValDeclaration ast) {
        ast.getDefinition().acceptVisitor(this, state);
        return null;
    }

    @Override
    public Void visit(EffectCheckState state, VarDeclaration ast) {
        ast.getDefinition().acceptVisitor(this, state);
        return null;
    }

    @Override
    public Void visit(EffectCheckState state, Application ast) {
        ast.getFunction().acceptVisitor(this, state);
        for (TypedAST t : ast.getArguments()) {
            t.acceptVisitor(this, state);
        }
        return null;
    }

    @Override
    public Void visit(EffectCheckState state, Assertion ast) {
        return null;
    }

    @Override
    public Void visit(EffectCheckState state, Assignment ast) {
        ast.getValue().acceptVisitor(this, state);
        ast.getNext().acceptVisitor(this, state);
        ast.getTarget().acceptVisitor(this, state);
        return null;
    }

    @Override
    public Void visit(EffectCheckState state, Case ast) {
        ast.getAST().acceptVisitor(this, state);
        return null;
    }

    @Override
    public Void visit(EffectCheckState state, Fn ast) {
        ast.getBody().acceptVisitor(this, state);
        return null;
    }

    @Override
    public Void visit(EffectCheckState state, Invocation ast) {
        return null;
    }

    @Override
    public Void visit(EffectCheckState state, LetExpr ast) {
        return null;
    }

    @Override
    public Void visit(EffectCheckState state, Match ast) {
        return null;
    }

    @Override
    public Void visit(EffectCheckState state, New ast) {
        return null;
    }

    @Override
    public Void visit(EffectCheckState state, TaggedInfo ast) {
        return null;
    }

    @Override
    public Void visit(EffectCheckState state, Variable ast) {
        return null;
    }

    @Override
    public Void visit(EffectCheckState state, BooleanConstant ast) {
        return null;
    }

    @Override
    public Void visit(EffectCheckState state, CharacterConstant ast) {
        return null;
    }

    @Override
    public Void visit(EffectCheckState state, FloatConstant ast) {
        return null;
    }

    @Override
    public Void visit(EffectCheckState state, IntegerConstant ast) {
        return null;
    }

    @Override
    public Void visit(EffectCheckState state, StringConstant ast) {
        return null;
    }

    @Override
    public Void visit(EffectCheckState state, UnitVal ast) {
        return null;
    }

    @Override
    public Void visit(EffectCheckState state, Script ast) {
        return null;
    }

    @Override
    public Void visit(EffectCheckState state, Sequence ast) {
        return null;
    }

    @Override
    public Void visit(EffectCheckState state, DSLLit ast) {
        return null;
    }
}