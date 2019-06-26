package wyvern.tools.typedAST.typedastvisitor;

import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.parsing.DSLLit;
import wyvern.tools.typedAST.core.Script;
import wyvern.tools.typedAST.core.Sequence;
import wyvern.tools.typedAST.core.binding.NameBindingImpl;
import wyvern.tools.typedAST.core.declarations.DeclSequence;
import wyvern.tools.typedAST.core.declarations.DefDeclaration;
import wyvern.tools.typedAST.core.declarations.ForwardDeclaration;
import wyvern.tools.typedAST.core.declarations.EffectDeclaration;
import wyvern.tools.typedAST.core.declarations.ImportDeclaration;
import wyvern.tools.typedAST.core.declarations.Instantiation;
import wyvern.tools.typedAST.core.declarations.ModuleDeclaration;
import wyvern.tools.typedAST.core.declarations.RecConstructDeclaration;
import wyvern.tools.typedAST.core.declarations.RecDeclaration;
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

import java.util.Iterator;

public class AnnotatedEffectVisitor extends TypedASTVisitor<GenContext, Void> {

    @Override
    public Void visit(GenContext state, NameBindingImpl ast) {
        return null;
    }

    @Override
    public Void visit(GenContext state, DeclSequence ast) {
        Sequence normalSeq = ast.filterNormal();
        Iterator<TypedAST> astIterator = normalSeq.flatten();
        while (astIterator.hasNext()) {
            TypedAST nextAST = astIterator.next();
            nextAST.acceptVisitor(this, state);
        }
        return null;
    }

    @Override
    public Void visit(GenContext state, DefDeclaration ast) {
        if (ast.getEffectSet(state) == null) {
            ast.setEmptyEffectSet();
        }
        return null;
    }

    @Override
    public Void visit(GenContext state, ForwardDeclaration ast) {
        return null;
    }

    @Override
    public Void visit(GenContext state, EffectDeclaration ast) {
        return null;
    }

    @Override
    public Void visit(GenContext state, ImportDeclaration ast) {
        return null;
    }

    @Override
    public Void visit(GenContext state, Instantiation ast) {
        return null;
    }

    @Override
    public Void visit(GenContext state, ModuleDeclaration ast) {
        ast.getInner().acceptVisitor(this, state);
        return null;
    }

    @Override
    public Void visit(GenContext state, TypeAbbrevDeclaration ast) {
        return null;
    }

    @Override
    public Void visit(GenContext state, TypeDeclaration ast) {
        return null;
    }

    @Override
    public Void visit(GenContext state, TypeVarDecl ast) {
        return null;
    }

    @Override
    public Void visit(GenContext state, ValDeclaration ast) {
        ast.getDefinition().acceptVisitor(this, state);
        return null;
    }

    @Override
    public Void visit(GenContext state, VarDeclaration ast) {
        ast.getDefinition().acceptVisitor(this, state);
        return null;
    }

    @Override
    public Void visit(GenContext state, Application ast) {
        ast.getFunction().acceptVisitor(this, state);
        for (TypedAST typedAST : ast.getArguments()) {
            typedAST.acceptVisitor(this, state);
        }
        return null;
    }

    @Override
    public Void visit(GenContext state, Assertion ast) {
        return null;
    }

    @Override
    public Void visit(GenContext state, Assignment ast) {
        ast.getValue().acceptVisitor(this, state);
        ast.getTarget().acceptVisitor(this, state);
        return null;
    }

    @Override
    public Void visit(GenContext state, Case ast) {
        ast.getAST().acceptVisitor(this, state);
        return null;
    }

    @Override
    public Void visit(GenContext state, Fn ast) {
        ast.getBody().acceptVisitor(this, state);
        return null;
    }

    @Override
    public Void visit(GenContext state, Invocation ast) {
        if (ast.getArgument() != null) {
            ast.getArgument().acceptVisitor(this, state);
        }
        if (ast.getArgument() != null) {
            ast.getReceiver().acceptVisitor(this, state);
        }
        return null;
    }

    @Override
    public Void visit(GenContext state, LetExpr ast) {
        ast.getBody().acceptVisitor(this, state);
        return null;
    }

    @Override
    public Void visit(GenContext state, Match ast) {
        ast.getMatchingOver().acceptVisitor(this, state);
        ast.getDefaultCase().getAST().acceptVisitor(this, state);
        for (Case cse : ast.getCases()) {
            cse.getAST().acceptVisitor(this, state);
        }
        return null;
    }

    @Override
    public Void visit(GenContext state, New ast) {
        ast.getDecls().acceptVisitor(this, state);
        return null;
    }

    @Override
    public Void visit(GenContext state, TaggedInfo ast) {
        return null;
    }

    @Override
    public Void visit(GenContext state, Variable ast) {
        return null;
    }

    @Override
    public Void visit(GenContext state, BooleanConstant ast) {
        return null;
    }

    @Override
    public Void visit(GenContext state, CharacterConstant ast) {
        return null;
    }

    @Override
    public Void visit(GenContext state, FloatConstant ast) {
        return null;
    }

    @Override
    public Void visit(GenContext state, IntegerConstant ast) {
        return null;
    }

    @Override
    public Void visit(GenContext state, StringConstant ast) {
        return null;
    }

    @Override
    public Void visit(GenContext state, UnitVal ast) {
        return null;
    }

    @Override
    public Void visit(GenContext state, Script ast) {
        return null;
    }

    @Override
    public Void visit(GenContext state, Sequence ast) {
        for (TypedAST exp : ast.getExps()) {
            exp.acceptVisitor(this, state);
        }
        return null;
    }

    @Override
    public Void visit(GenContext state, DSLLit ast) {
        return null;
    }

  @Override
  public Void visit(GenContext state, RecConstructDeclaration ast) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Void visit(GenContext state, RecDeclaration ast) {
    // TODO Auto-generated method stub
    return null;
  }
}
