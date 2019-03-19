package wyvern.tools.typedAST.core.expressions;

import java.util.List;

import wyvern.target.corewyvernIL.BindingSite;
import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.expression.IExpr;
import wyvern.target.corewyvernIL.modules.TypedModuleSpec;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.type.NominalType;
import wyvern.target.corewyvernIL.type.RefinementType;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.typedAST.core.binding.NameBinding;
import wyvern.tools.typedAST.core.binding.NameBindingImpl;
import wyvern.tools.typedAST.interfaces.ExpressionAST;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.types.Type;

public class Case {

    private ExpressionAST ast;
    private Type taggedType;
    private NameBinding binding; // may be null if we are not binding a name
    //TODO refactor this class into two classes for each type?
    private CaseType caseType;

    /**
     * Instantiates a new Case statement, which matches over the given tagged type name,
     * binding a variable and executing the given AST.
     *
     * @param name the name of the case type
     * @param taggedType the type of the case instance
     * @param ast the body of the expression to be executed
     */
    public Case(String name, Type taggedType, TypedAST ast) {
        this(taggedType, (ExpressionAST) ast);
        this.binding = new NameBindingImpl(name, taggedType);
    }

    /**
     * Instantiates a new Case statement, which matches over the given tagged type name,
     * executing the given AST.
     *
     * @param taggedType the type of the case instance
     * @param ast the body of the expression to be executed
     */
    public Case(Type taggedType, TypedAST ast) {
        this.taggedType = taggedType;
        this.ast = (ExpressionAST) ast;
        this.caseType = CaseType.TYPED;
    }

    /**
     * Instantiates a new default Case statement, which is executed if no others match.
     *
     * @param ast the body of the expression to be executed
     */
    public Case(TypedAST ast) {
        this.ast = (ExpressionAST) ast;
        this.caseType = CaseType.DEFAULT;
    }

    /**
     * toString generates a String representation of this Case, and embeds the tag as well.
     */
    public String toString() {
        if (taggedType != null) {
            return "Case " + taggedType.toString() + " with expression: " + ast;
        } else {
            return "Case with null taggedType and ast = " + ast;
        }
    }

    public Type getTaggedTypeMatch() {
        return taggedType;
    }

    public ExpressionAST getAST() {
        return ast;
    }

    public CaseType getCaseType() {
        return caseType;
    }

    public boolean isDefault() {
        return caseType == CaseType.DEFAULT;
    }

    public boolean isTyped() {
        return caseType == CaseType.TYPED;
    }

    public wyvern.target.corewyvernIL.Case generateILCase(
                GenContext ctx,
                ValueType matchType,
                IExpr matchExpr,
                ValueType expectedType,
                List<TypedModuleSpec> dependencies) {
        String bindingVar = binding.getName();
        BindingSite bindingSite = new BindingSite(bindingVar);
        wyvern.target.corewyvernIL.expression.Variable expr = new wyvern.target.corewyvernIL.expression.Variable(bindingVar);
        ValueType vt = taggedType == null ? null : taggedType.getILType(ctx);
        ValueType bestType = vt;
        // Figure out the best type to use here between the match type and the tag
        if (!(matchType != null && vt != null && vt.isSubtypeOf(matchType, ctx, null))) {
            bestType = vt == null ? matchType : vt;
        }
        if (bestType instanceof RefinementType) {
            bestType = ((RefinementType) bestType).getBase();
            System.err.println("Ignoring refinement in case branch in " + taggedType.getLocation());
        }
        ValueType adaptedPattern = wyvern.target.corewyvernIL.Case.getAdaptedPattern((NominalType) bestType, matchType, matchExpr, ctx);
        ctx = ctx.extend(bindingSite, expr, adaptedPattern);
        Expression body = (Expression) ast.generateIL(ctx, expectedType, dependencies);
        return new wyvern.target.corewyvernIL.Case(bindingSite, (NominalType) bestType, body);
    }

    /**
     * The different types a case can be.
     *
     * @author troy
     */
    public enum CaseType {
        // If the user specifies no type to match against.
        DEFAULT,
        // If the user specifies a type to match against.
        TYPED
    }
}
