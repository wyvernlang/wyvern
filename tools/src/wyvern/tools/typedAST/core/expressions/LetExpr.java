package wyvern.tools.typedAST.core.expressions;

import java.util.Iterator;
import java.util.List;

import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.modules.TypedModuleSpec;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.typedAST.abs.AbstractExpressionAST;
import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.core.declarations.DeclSequence;
import wyvern.tools.typedAST.interfaces.CoreAST;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.typedastvisitor.TypedASTVisitor;

public class LetExpr extends AbstractExpressionAST implements CoreAST {
    private DeclSequence decl;
    private TypedAST body;

    public LetExpr(DeclSequence decl, TypedAST body) {
        this.decl = decl;
        this.body = body;
    }

    /*
    // TODO: SMELL: maybe should have a list of decls here? but reuse between letexpr and class?
    // depends on declToAdd adding the returned decl to itself
    public Declaration addDecl(Declaration declToAdd) {
        Declaration old = decl;
        decl = declToAdd;
        return old;
    }
    public Declaration getDecl() {
        return decl;
    }
     */


    public TypedAST getBody() {
        return body;
    }

    private FileLocation location = FileLocation.UNKNOWN;
    public FileLocation getLocation() {
        return this.location;
    }

    @Override
    public <S, T> T acceptVisitor(TypedASTVisitor<S, T> visitor, S state) {
        return visitor.visit(state, this);
    }

    @Override
    public Expression generateIL(GenContext ctx, ValueType expectedType, List<TypedModuleSpec> dependencies) {
        final Iterator<Declaration> declIter = decl.getDeclIterator().iterator();
        Iterator<TypedAST> myIter = new Iterator<TypedAST>() {
            private boolean returnedBody = false;

            @Override
            public boolean hasNext() {
                return !returnedBody;
            }

            @Override
            public TypedAST next() {
                if (declIter.hasNext()) {
                    return declIter.next();
                }
                returnedBody = true;
                return body;
            }
        };
        throw new RuntimeException("not implemented");
        //return GenUtil.doGenModuleIL(ctx, ctx, ctx, myIter, false);
        /*
        if (!declIter.hasNext()) {
            throw new RuntimeException("oops, no decls in the let");
        }
        Declaration d = declIter.next();
        if (declIter.hasNext()) {
            throw new RuntimeException("only handle lets with one decl for now");
        }
        if (d instanceof ValDeclaration) {
            ValDeclaration vd = (ValDeclaration) d;
            String name = vd.getName();
            return new Let(name, vd.getDefinition().generateIL(ctx),
                    body.generateIL(ctx.extend(name, new wyvern.target.corewyvernIL.expression.Variable(name))));
        } else {
            throw new RuntimeException("only handle val decls for now");
        }
        */
    }
}
