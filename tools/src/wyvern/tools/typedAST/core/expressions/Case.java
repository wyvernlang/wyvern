package wyvern.tools.typedAST.core.expressions;

import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.ToolError;
import wyvern.tools.typedAST.core.binding.NameBinding;
import wyvern.tools.typedAST.core.binding.NameBindingImpl;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.UnresolvedType;

public class Case {

    private TypedAST ast;
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
        this(taggedType, ast);
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
        this.ast = ast;
        this.caseType = CaseType.TYPED;
    }

    /**
     * Instantiates a new default Case statement, which is executed if no others match.
     *
     * @param ast the body of the expression to be executed
     */
    public Case(TypedAST ast) {
        this.ast = ast;
        this.caseType = CaseType.DEFAULT;
    }

    /**
      * resolve converts this case expression to the corresponding tagged type and resolves that
      *
      * @param env the Environment in which to resolve the case.
      * @param m the Match to resolve against.
      */
    public void resolve(Environment env, Match m) {
        if (taggedType instanceof UnresolvedType) {
            String name = ((UnresolvedType) taggedType).getName();
            if (env.lookup(name) == null && env.lookupType(name) == null) {
                ToolError.reportError(ErrorMessage.TYPE_NOT_DECLARED, m, name);
            }

            this.taggedType = ((UnresolvedType) taggedType).resolve(env);
        }
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

    public TypedAST getAST() {
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
