package wyvern.target.corewyvernIL.support;

import static wyvern.tools.errors.ErrorMessage.VARIABLE_NOT_DECLARED;

import wyvern.target.corewyvernIL.BindingSite;
import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.expression.IExpr;
import wyvern.target.corewyvernIL.expression.Path;
import wyvern.target.corewyvernIL.expression.Variable;
import wyvern.target.corewyvernIL.type.NominalType;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.ToolError;
import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.core.declarations.EffectDeclaration;
import wyvern.tools.typedAST.core.declarations.TypeAbbrevDeclaration;
import wyvern.tools.typedAST.core.declarations.TypeVarDecl;
import wyvern.tools.typedAST.interfaces.TypedAST;

public abstract class GenContext extends TypeContext {

    private GenContext nextContext = null;

    protected GenContext(GenContext next) {
        nextContext = next;
    }

    /** Extends the context with a variable-type mapping
     * returns the functional extension. */
    public GenContext extend(BindingSite varBinding, ValueType type) {
        return new VarGenContext(varBinding, new Variable(varBinding), type, this);
    }

    /** Extends the context with a variable-type mapping, where uses of the
     * variable are transformed to some possibly more complex expression
     * (typically var.field); returns the functional extension. */
    public GenContext extend(BindingSite varBinding, Expression expr, ValueType type) {
        return new VarGenContext(varBinding, expr, type, this);
    }

    /** Extends the context with a variable-type mapping, where uses of the
     * variable are transformed to some possibly more complex expression
     * (typically var.field); returns the functional extension. */
    public GenContext extend(String varName, Expression expr, ValueType type) {
        return new VarGenContext(varName, expr, type, this);
    }

    /** Looks up an expression to use in translation when varName occurs in the source
     *
     * @param varName   the name to look up
     * @param loc       the location to report in an error message, if the name is not found
     * @return
     */
    public final IExpr lookupExp(String varName, FileLocation loc) {
        try {
            return getCallableExpr(varName).genExpr(loc);
        } catch (RuntimeException e) {
            ToolError.reportError(VARIABLE_NOT_DECLARED, loc, varName);
            throw new RuntimeException("impossible");
        }
    }

    public final ValueType lookupType(String typeName, FileLocation loc) {
        Path objName = getContainerForTypeAbbrev(typeName);
        if (objName == null) {
            ToolError.reportError(ErrorMessage.TYPE_NOT_DEFINED, loc, typeName);
        }
        return new NominalType(objName, typeName, loc);
    }


    /**
     * Gets the name of the container object for a type abbreviation.
     * For a mapping T -> y.T, getContainerForTypeAbbrev(T) = y;
     *
     * @param typeName the type abbreviation's name
     * @return the name of the container object for the type abbreviation
     */
    public abstract Path getContainerForTypeAbbrev(String typeName);

    public static GenContext empty() {
        return theEmpty;
    }

    protected abstract String endToString();

    private static final GenContext theEmpty = new EmptyGenContext();

    public static String generateName() {
        return "var_" + (count++);
    }
    private static int count = 0;

    /**
     * Adding mapping for a declaration may include recursive calls.
     * if ast is a method declaration f then add f->y.f in the mapping
     * if ast is a type declaration T then add T->y.T in the mapping
     * where y is an object:
     * y = new { IL declarations };
     *
     * @param newName the generated new name to symbolize the outer object
     * @param ast the declaration of Wyvern Module System
     * @return a new functional environment which extends the mapping
     */
    public GenContext rec(BindingSite site, TypedAST ast) {
        if (ast instanceof TypeVarDecl || ast instanceof EffectDeclaration) {
            String typeName = ((Declaration) ast).getName();
            return new TypeOrEffectGenContext(typeName, site, this);
        } else if (ast instanceof wyvern.tools.typedAST.core.declarations.DefDeclaration) {
            //assert (ast instanceof wyvern.tools.typedAST.core.declarations.DefDeclaration);
            wyvern.tools.typedAST.core.declarations.DefDeclaration methodDecl = (wyvern.tools.typedAST.core.declarations.DefDeclaration) ast;
            String methodName = methodDecl.getName();
            return new MethodGenContext(methodName, site, this, ast.getLocation());
        } else {
            assert (ast instanceof TypeAbbrevDeclaration);
            TypeAbbrevDeclaration typeAbbrevDecl = (TypeAbbrevDeclaration) ast;
            return new TypeOrEffectGenContext(typeAbbrevDecl.getName(), site, this);
        }
    }

    /**
     * Internal recursive version.
     *
     * @param varName the method name
     * @param origCtx the original context the lookupValue was performed in
     * @return the CallableExprGenerator
     */
    abstract CallableExprGenerator getCallableExprRec(String varName, GenContext origCtx);

    /**
     * Gets a CallableExprGenerator for the variable.
     * This is used to produce more efficient code in the case where
     * the variable is a method, so that the method does not need to
     * be eta-expanded when it is to be called directly.
     *
     * @param varName the method name
     * @return the CallableExprGenerator
     */
    public final CallableExprGenerator getCallableExpr(String varName) {
        return getCallableExprRec(varName, this);
    }

    @Override
    protected GenContext getNext() {
        return nextContext;
    }

    public InterpreterState getInterpreterState() {
        return this.nextContext.getInterpreterState();
    }
}
