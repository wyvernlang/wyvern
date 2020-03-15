package wyvern.tools.typedAST.core.expressions;

import java.util.LinkedList;
import java.util.List;

import wyvern.target.corewyvernIL.FormalArg;
import wyvern.target.corewyvernIL.decl.Declaration;
import wyvern.target.corewyvernIL.decl.DefDeclaration;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.decltype.DefDeclType;
import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.expression.IExpr;
import wyvern.target.corewyvernIL.expression.New;
import wyvern.target.corewyvernIL.modules.TypedModuleSpec;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.support.TopLevelContext;
import wyvern.target.corewyvernIL.support.Util;
import wyvern.target.corewyvernIL.type.DynamicType;
import wyvern.target.corewyvernIL.type.StructuralType;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.ToolError;
import wyvern.tools.typedAST.abs.AbstractExpressionAST;
import wyvern.tools.typedAST.core.binding.NameBinding;
import wyvern.tools.typedAST.interfaces.BoundCode;
import wyvern.tools.typedAST.interfaces.CoreAST;
import wyvern.tools.typedAST.interfaces.ExpressionAST;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.typedastvisitor.TypedASTVisitor;

public class Fn extends AbstractExpressionAST implements CoreAST, BoundCode {
    public static final String LAMBDA_STRUCTUAL_DECL = "@lambda-structual-decl";
    private List<NameBinding> bindings;
    private ExpressionAST body;
    private FileLocation location = FileLocation.UNKNOWN;

    /**
     * Creates a new function with the argument bindings and the AST node pointing to the body.
     *
     * @param bindings The arguments to the function call
     * @param body the body of the function
     * @param loc the location in the source code where this function is defined.
     */
    public Fn(List<NameBinding> bindings, TypedAST body, FileLocation loc) {
        this.bindings = bindings;
        this.body = (ExpressionAST) body;
        this.location = loc;
    }

    @Override
    public List<NameBinding> getArgBindings() {
        return bindings;
    }

    @Override
    public <S, T> T acceptVisitor(TypedASTVisitor<S, T> visitor, S state) {
        return visitor.visit(state, this);
    }

    @Override
    public TypedAST getBody() {
        return body;
    }

    public FileLocation getLocation() {
        return this.location;
    }

    @Override
    /**
     * @param GenContext The type context of the lambda declaration
     * @return The Intermediate Representation of the inline function decl
     */
    public Expression generateIL(
            GenContext ctx,
            ValueType expectedType,
            List<TypedModuleSpec> dependencies) {
        /*
         * First, map the NameBindings to Formal Arguments, dropping the parameters into the IR.
         * Next, find the type of the body. The type of the body is the return type of the function.
         * This allows the creation of the DefDeclaration
         *
         * Next, create a new StructuralType, duplicating the DefDecl as a DeclType.
         * Use the StructualType and the DefDeclaration to make a New. Return.
         */

        // Convert the bindings into formals
        List<FormalArg> intermediateArgs = convertBindingToArgs(
                this.bindings,
                ctx,
                expectedType
                );

        // Extend the generalContext to include the parameters passed into the function.
        ctx = extendCtxWithParams(ctx, intermediateArgs);

        // Generate the IL for the body, and get its return type.
        ValueType expectedBodyType = (expectedType == null)
                ? null : Fn.getExpectedResult(ctx, expectedType);
        IExpr il = this.body.generateIL(ctx, expectedBodyType, dependencies);
        ValueType bodyReturnType = il.typeCheck(ctx, null);

        // Create a new list of function declaration,
        // which is a singleton, containing only Util.APPLY_NAME
        DefDeclaration applyDef = new DefDeclaration(
                Util.APPLY_NAME,
                intermediateArgs,
                bodyReturnType,
                il,
                getLocation()
                );
        List<Declaration> declList = new LinkedList<>();
        declList.add(applyDef);

        // Store a redundency of the function declaration
        DeclType ddecl = new DefDeclType(Util.APPLY_NAME, bodyReturnType, intermediateArgs);
        List<DeclType> declTypes = new LinkedList<>();
        declTypes.add(ddecl);

        // set up containsResources() properly by typechecking applyDef
        applyDef.typeCheck(ctx, ctx);
        StructuralType newType = new StructuralType(
                LAMBDA_STRUCTUAL_DECL,
                declTypes,
                applyDef.containsResource(ctx)
                );

        return new New(declList, newType.getSelfSite(), newType, getLocation());
    }

    public  void genTopLevel(TopLevelContext topLevelContext, ValueType expectedType) {
        final Expression exp = generateIL(topLevelContext.getContext(), expectedType, null);
        topLevelContext.addExpression(exp, expectedType);
    }

    private List<FormalArg> convertBindingToArgs(
            List<NameBinding> bindings,
            GenContext ctx,
            ValueType declType) {

        List<FormalArg> expectedFormals =
                declType == null ? null : getExpectedFormls(ctx, declType, location);

        List<FormalArg> result = new LinkedList<FormalArg>();

        if (expectedFormals != null && expectedFormals.size() != bindings.size()) {
            final int expectedSize = expectedFormals.size();
            if (expectedSize == 0) {
                ToolError.reportError(ErrorMessage.SYNTAX_FOR_NO_ARG_LAMBDA, this);
            } else {
                ToolError.reportError(
                        ErrorMessage.WRONG_NUMBER_OF_ARGUMENTS,
                        this,
                        Integer.toString(expectedSize),
                        Integer.toString(bindings.size())
                        );
            }
        }

        for (int i = 0; i < bindings.size(); i++) {
            NameBinding binding = bindings.get(i);
            ValueType argType = null;

            if (binding.getType() != null) {
                argType = binding.getType().getILType(ctx);
            } else {
                if (expectedFormals == null) {
                    ToolError.reportError(ErrorMessage.CANNOT_INFER_ARG_TYPE, this);
                }
                argType = expectedFormals.get(i).getType();
            }

            result.add(new FormalArg(
                    binding.getName(),
                    argType
                    )
                    );
        }



        return result;
    }

    /** Returns the expected types of the formals, or null if the expected type is dyn. */
    private static List<FormalArg> getExpectedFormls(GenContext ctx, ValueType declType, FileLocation location) {
        if (declType.getCanonicalType(ctx) instanceof DynamicType) {
            return null;
        }
        StructuralType declStructuralType = declType.getStructuralType(ctx);

        DeclType applyDecl = declStructuralType.findDecl(Util.APPLY_NAME, ctx);

        if (applyDecl == null || !(applyDecl instanceof DefDeclType)) {
            // there is some imprecision in the expected type; just return null and force the programmer to be explicit
            return null;
            //ToolError.reportError(ErrorMessage.TYPE_CANNOT_BE_APPLIED, location,
            //                      "the declType (" + declType + ") is not a lambda type (it has no apply method)");
        }

        DefDeclType applyDef = (DefDeclType) applyDecl;

        return applyDef.getFormalArgs();
    }

    /** Returns the expected type of the result, or null if the expected type is dyn. */
    private static ValueType getExpectedResult(GenContext ctx, ValueType declType) {
        if (declType.getCanonicalType(ctx) instanceof DynamicType) {
            return null;
        }
        StructuralType declStructuralType = declType.getStructuralType(ctx);

        DeclType applyDecl = declStructuralType.findDecl(Util.APPLY_NAME, ctx);

        if (applyDecl == null || !(applyDecl instanceof DefDeclType)) {
            // there is some imprecision in the expected type; just return null and force the programmer to be explicit
            return null;
            //throw new RuntimeException("the declType is not a lambda type(it has no apply method)");
        }

        DefDeclType applyDef = (DefDeclType) applyDecl;

        return applyDef.getRawResultType();
    }

    private static GenContext extendCtxWithParams(GenContext ctx, List<FormalArg> formalArgs) {
        for (FormalArg binding : formalArgs) {
            ctx = ctx.extend(
                    binding.getName(),
                    new wyvern.target.corewyvernIL.expression.Variable(binding.getName()),
                    binding.getType()
                    );
        }
        return ctx;
    }
    
    @Override
    public StringBuilder prettyPrint() {
        StringBuilder sb = new StringBuilder();
        sb.append("fn (").append(bindings.size()).append(") =>\n");
        sb.append(body.prettyPrint());
        return sb;
    }
}
