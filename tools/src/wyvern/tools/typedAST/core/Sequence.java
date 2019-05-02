package wyvern.tools.typedAST.core;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import wyvern.target.corewyvernIL.expression.IExpr;
import wyvern.target.corewyvernIL.modules.TypedModuleSpec;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.support.TopLevelContext;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.typedAST.abs.AbstractExpressionAST;
import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.core.declarations.DeclSequence;
import wyvern.tools.typedAST.core.declarations.DefDeclaration;
import wyvern.tools.typedAST.core.declarations.EffectDeclaration;
import wyvern.tools.typedAST.core.declarations.TypeAbbrevDeclaration;
import wyvern.tools.typedAST.core.declarations.TypeVarDecl;
import wyvern.tools.typedAST.core.declarations.VarDeclaration;
import wyvern.tools.typedAST.core.expressions.Fn;
import wyvern.tools.typedAST.core.values.UnitVal;
import wyvern.tools.typedAST.interfaces.CoreAST;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.typedAST.typedastvisitor.TypedASTVisitor;
import wyvern.tools.types.Type;

public class Sequence extends AbstractExpressionAST implements CoreAST, Iterable<TypedAST> {
    private LinkedList<TypedAST> exps = new LinkedList<TypedAST>();
    private Type retType = null;

    private TypedAST check(TypedAST e) {
        //        if (e == null) {
        //            throw new RuntimeException("no null values in Sequence");
        //        }
        return e;
    }

    public static Sequence append(Sequence s, TypedAST e) {
        Iterator<TypedAST> innerIter = s.iterator();
        if (s instanceof DeclSequence && e instanceof Declaration) {
            return DeclSequence.simplify(new DeclSequence(s, e));
        }
        return new Sequence(() -> new Iterator<TypedAST>() {
            private boolean fetched = false;
            @Override
            public boolean hasNext() {
                if (innerIter.hasNext()) {
                    return true;
                }
                return !fetched;
            }

            @Override
            public TypedAST next() {
                if (innerIter.hasNext()) {
                    return innerIter.next();
                }
                if (!fetched) {
                    fetched = true;
                    return e;
                }
                throw new RuntimeException();
            }
        });
    }

    public Sequence(TypedAST first) {
        exps.add(check(first));
    }
    public Sequence(Iterable<TypedAST> first) {
        exps = new LinkedList<TypedAST>();
        for (TypedAST elem : first) {
            exps.add(check(elem));
        }
    }

    public Sequence(TypedAST first, TypedAST second) {
        if (first instanceof Sequence) {
            exps.addAll(((Sequence) first).exps);
        } else if (first != null) {
            exps.add(check(first));
        }
        if (second instanceof Sequence) {
            exps.addAll(((Sequence) second).exps);
        } else if (second != null) {
            exps.add(check(second));
        }
    }

    public Sequence() {
        // TODO Auto-generated constructor stub
    }
    private FileLocation location = FileLocation.UNKNOWN;
    public FileLocation getLocation() {
        return this.location;
    }

    public String toString() {
        return this.exps.toString();
    }

    @Override
    public Iterator<TypedAST> iterator() {
        return exps.iterator();
    }

    // FIXME: Hack. Flattens decl sequence. NEED TO REFACTOR!
    public Iterable<Declaration> getDeclIterator() {

        final Iterator<TypedAST> inner = flatten();
        return new Iterable<Declaration>() {

            @Override
            public Iterator<Declaration> iterator() {
                return new Iterator<Declaration>() {
                    @Override
                    public boolean hasNext() {
                        return inner.hasNext();
                    }

                    @Override
                    public Declaration next() {
                        return (Declaration) inner.next();
                    }

                    @Override
                    public void remove() {
                        inner.remove();
                    }
                };
            }

        };
    }

    public <T extends TypedAST> Iterable<T> getIterator() {
        final Iterator<TypedAST> inner = iterator();
        return new Iterable<T>() {

            @Override
            public Iterator<T> iterator() {
                return new Iterator<T>() {

                    @Override
                    public boolean hasNext() {
                        return inner.hasNext();
                    }

                    @Override
                    public T next() {
                        return (T) inner.next();
                    }

                    @Override
                    public void remove() {
                        inner.remove();

                    }
                };
            }

        };
    }

    public List<TypedAST> getExps() {
        return exps;
    }

    public TypedAST getLast() {
        return exps.getLast();
    }
    public int size() {
        return exps.size();
    }

    public Iterator<TypedAST> flatten() {
        final Iterator<TypedAST> internal = this.getIterator().iterator();

        return new Iterator<TypedAST>() {
            private Stack<Iterator<TypedAST>> iterstack = new Stack<>();
            {
                iterstack.push(internal);
            }
            private TypedAST lookahead;

            @Override
            public boolean hasNext() {
                if (!iterstack.empty()) {
                    if (!iterstack.peek().hasNext()) {
                        iterstack.pop();
                        return hasNext();
                    }
                    lookahead = iterstack.peek().next();
                    if (lookahead == null) {
                        iterstack.peek().hasNext();
                        throw new RuntimeException("null lookahead!");
                    }
                    if (lookahead instanceof Sequence) {
                        iterstack.push(((Sequence) lookahead).iterator());
                        lookahead = null;
                        return hasNext();
                    }
                    return true;
                }
                return false;
            }

            @Override
            public TypedAST next() {
                if (lookahead != null) {
                    TypedAST res = lookahead;
                    lookahead = null;
                    return res;
                }
                return iterstack.peek().next();
            }
        };
    }

    @Override
    public <S, T> T acceptVisitor(TypedASTVisitor<S, T> visitor, S state) {
        return visitor.visit(state, this);
    }

    @Override
    public IExpr generateIL(GenContext ctx, ValueType expectedType, List<TypedModuleSpec> dependencies) {
        Sequence seqWithBlocks = combine();
        TopLevelContext tlc = new TopLevelContext(ctx, expectedType);
        seqWithBlocks.genTopLevel(tlc, expectedType);
        if (tlc.getDependencies().size() > 0) {
            dependencies.addAll(tlc.getDependencies());
        }
        return tlc.getExpression();
    }

    public boolean hasVarDeclaration() {
        for (TypedAST e : exps) {
            if (e instanceof VarDeclaration) {
                return true;
            }
        }
        return false;
    }

    /**
     * Generate IL expression for a top-level declaration sequence</br>
     * @see GenUtil.doGenModuleIL
     *
     * @param ctx the context
     * @param isModule whether it is actually a module expression:
     *                 true for the body of a module, false for the body of a script
     * @return the IL expression of a module
     */
    public IExpr generateModuleIL(GenContext ctx, boolean isModule) {
        // group blocks of mutually-recursive declarations
        Sequence seqWithBlocks = combine();

        // generate a top-level context to help with translation
        TopLevelContext tlc = new TopLevelContext(ctx, null);

        // do the actual translation
        seqWithBlocks.genTopLevel(tlc);
        IExpr result = isModule ? tlc.getModuleExpression() : tlc.getExpression();
        return result;
    }

    @Override
    public void genTopLevel(TopLevelContext tlc) {
        for (TypedAST ast : exps) {
            ast.genTopLevel(tlc);
            if (ast instanceof Declaration) {
                ((Declaration) ast).addModuleDecl(tlc);
            }
        }
    }


    public void genTopLevel(TopLevelContext tlc, ValueType expectedType) {
        for (int i = 0; i < exps.size() - 1; i++) {
            TypedAST ast =  exps.get(i);
            ast.genTopLevel(tlc);
            if (ast instanceof Declaration) {
                ((Declaration) ast).addModuleDecl(tlc);
            }
        }

        TypedAST ast = exps.getLast();
        if (ast instanceof Fn) {
            ((Fn) ast).genTopLevel(tlc, expectedType);
        } else if (ast instanceof Declaration) { // Add a unit value on the end, so the declaration evaluates to Unit.
            Value v = UnitVal.getInstance(this.getLocation());
            ast.genTopLevel(tlc);
            v.genTopLevel(tlc);
        } else {
            ast.genTopLevel(tlc);
        }

        if (ast instanceof Declaration) {
            ((Declaration) ast).addModuleDecl(tlc);
        }
    }

    /**
     * A filter for the sequence </br>
     * Combines the sequential type and method declarations into a block </br>
     * @return return the sequence after combination.
     */
    Sequence combine() {
        boolean recBlock = false;
        Sequence normalSeq = new Sequence();
        Sequence recSequence = new DeclSequence(); // not sure definition is needed here (Valerie)
        for (TypedAST ast : this.getIterator()) {

            if (ast instanceof TypeVarDecl || ast instanceof DefDeclaration || ast instanceof TypeAbbrevDeclaration || ast instanceof EffectDeclaration) {
                Declaration d = (Declaration) ast;
                if (!recBlock) { // no open recSequence
                    recBlock = true;
                    recSequence = new DeclSequence(); // start one
                }
                recSequence = Sequence.append(recSequence, d); // add the declaration
            } else {
                if (recBlock) { // still collecting for the recSequence
                    normalSeq = Sequence.append(normalSeq, recSequence); // add it to the normalSeq
                }
                normalSeq = Sequence.append(normalSeq, ast); // add this not-mutually recursive ast
                recBlock = false; // the recSequence has ended (need to begin another one if encountering 1 of the 3)
            }
        }

        if (recBlock) {
            normalSeq = Sequence.append(normalSeq, recSequence);
        }
        return normalSeq;
    }

    public StringBuilder prettyPrint() {
        StringBuilder sb = new StringBuilder();
        String rtStr = "null";
        if (retType != null) {
            rtStr = retType.toString();
        }
        sb.append("Sequence(" + rtStr + ", [");
        String sep = "";
        for (TypedAST ast : exps) {
            sb.append(sep);
            sb.append(ast.prettyPrint());
            sep = ", ";
        }
        sb.append("])");
        return sb;
    }
}
