package wyvern.tools.types;

import wyvern.target.corewyvernIL.expression.FieldGet;
import wyvern.target.corewyvernIL.expression.IExpr;
import wyvern.target.corewyvernIL.expression.Path;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.type.NominalType;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.ToolError;
import wyvern.tools.typedAST.core.expressions.Invocation;
import wyvern.tools.typedAST.core.expressions.Variable;
import wyvern.tools.typedAST.interfaces.ExpressionAST;
import wyvern.tools.typedAST.interfaces.TypedAST;

public class QualifiedType extends AbstractTypeImpl implements NamedType {
    private ExpressionAST base;
    private String name;

    @Override
    public String toString() {
        return getFullName();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getFullName() {
        return base.toString() + "." + name;
    }

    public TypedAST getBase() {
        return base;
    }

    @Override
    public FileLocation getLocation() {
        FileLocation loc = super.getLocation();
        if (loc == null) {
            loc = getBase().getLocation();
        }
        return loc;
    }

    public QualifiedType(TypedAST base, String name) {
        this.name = name;
        this.base = (ExpressionAST) base;
    }

    public QualifiedType(ExpressionAST base, String name) {
        this.name = name;
        this.base = base;
    }

    @Override
    public ValueType getILType(GenContext ctx) {
        return new NominalType(getPath(base, ctx), name, getLocation());
    }

    private static Path getPathOld(ExpressionAST ast, GenContext ctx) {
        Path path2 = getPath(ast, ctx);
        IExpr exp = ast.generateIL(ctx, null, null);
        if (!(exp instanceof Path)) {
            ToolError.reportError(ErrorMessage.QUALIFIED_TYPES_ONLY_FIELDS, ast);
        }
        if (!exp.equals(path2)) {
            throw new RuntimeException("surprise!");
        }
        return (Path) exp;
    }
    /** Gets a Path from an ExpressionAST without typechecking.
     * We do still take a context for looking up variable renamings.
     * Reports an ErrorMessage.QUALIFIED_TYPES_ONLY_FIELDS if the ExpressionAST
     * is not a variable with field dereferences after it.
     * We postpone checking that the fields are immutable until later.
     * */
    private static Path getPath(ExpressionAST ast, GenContext ctx) {
        if (ast instanceof Variable) {
            IExpr exp = ast.generateIL(ctx, null, null);
            if (!(exp instanceof Path)) {
                ToolError.reportError(ErrorMessage.QUALIFIED_TYPES_ONLY_FIELDS, ast);
            }
            return (Path) exp;
        } else if (ast instanceof Invocation) {
            Invocation i = (Invocation) ast;
            Path subPath = getPath(i.getReceiver(), ctx);
            return new FieldGet(subPath, i.getOperationName(), i.getLocation());
        } else {
            ToolError.reportError(ErrorMessage.QUALIFIED_TYPES_ONLY_FIELDS, ast);
        }
        throw new RuntimeException("can't get here");
    }

    @Override
    public boolean isPresent(GenContext ctx) {
        ExpressionAST a = base;
        while (a instanceof Invocation) {
            Invocation i = (Invocation) a;
            a = i.getReceiver();
        }
        return ctx.isPresent(((Variable) a).getName(), true);
    }
}
