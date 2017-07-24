package wyvern.tools.typedAST.core.expressions;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
import wyvern.tools.typedAST.abs.CachingTypedAST;
import wyvern.tools.typedAST.core.binding.NameBinding;
import wyvern.tools.typedAST.core.binding.NameBindingImpl;
import wyvern.tools.typedAST.core.evaluation.Closure;
import wyvern.tools.typedAST.interfaces.BoundCode;
import wyvern.tools.typedAST.interfaces.CoreAST;
import wyvern.tools.typedAST.interfaces.ExpressionAST;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.TypeResolver;
import wyvern.tools.types.extensions.Arrow;
import wyvern.tools.types.extensions.Unit;
import wyvern.tools.util.EvaluationEnvironment;

public class Fn extends CachingTypedAST implements CoreAST, BoundCode {
    public static final String LAMBDA_STRUCTUAL_DECL = "@lambda-structual-decl";
    private List<NameBinding> bindings;
    private ExpressionAST body;
    private FileLocation location = FileLocation.UNKNOWN;

    @Deprecated
    public Fn(List<NameBinding> bindings, TypedAST body) {
        this(bindings, body, FileLocation.UNKNOWN);
    }

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
    protected Type doTypecheck(Environment env, Optional<Type> expected) {
        Type argType = null;
        for (int i = 0; i < bindings.size(); i++) {
            NameBinding bdgs = bindings.get(i);
            bindings.set(
                    i,
                    new NameBindingImpl(
                        bdgs.getName(),
                        TypeResolver.resolve(bdgs.getType(), env)
                    )
            );
        }

        if (bindings.size() == 0) {
            argType = new Unit();
        } else if (bindings.size() == 1) {
            argType = bindings.get(0).getType();
        } else {
            // TODO: implement multiple args
            throw new RuntimeException("tuple args not implemented");
        }

        Environment extEnv = env;
        for (NameBinding bind : bindings) {
            extEnv = extEnv.extend(bind);
        }

        Type resultType = body.typecheck(extEnv, expected.map(exp -> ((Arrow)exp).getResult()));
        return new Arrow(argType, resultType);
    }

    @Override
    @Deprecated
    public Value evaluate(EvaluationEnvironment env) {
        return new Closure(this, env);
    }

    @Override
    public List<NameBinding> getArgBindings() {
        return bindings;
    }

    @Override
    public TypedAST getBody() {
        return body;
    }

    @Override
    public Map<String, TypedAST> getChildren() {
        Hashtable<String, TypedAST> children = new Hashtable<>();
        children.put("body", body);
        return children;
    }

    @Override
    public ExpressionAST doClone(Map<String, TypedAST> nc) {
        return new Fn(bindings, nc.get("body"), this.location);
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
                                     ? null : this.getExpectedResult(ctx, expectedType);
        IExpr il = this.body.generateIL(ctx, expectedBodyType, dependencies);
        ValueType bodyReturnType = il.typeCheck(ctx);

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
        ValueType newType = new StructuralType(
            LAMBDA_STRUCTUAL_DECL,
            declTypes,
            applyDef.containsResource(ctx)
        );

        return new New(declList, "@lambda-decl", newType, getLocation());
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
            declType == null ? null : getExpectedFormls(ctx, declType);

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

            result.add( new FormalArg(
                    binding.getName(),
                    argType
                )
            );
        }
        
        

        return result;
    }

    /** Returns the expected types of the formals, or null if the expected type is dyn. */
    private static List<FormalArg> getExpectedFormls(GenContext ctx, ValueType declType) {
        if (declType.getCanonicalType(ctx) instanceof DynamicType) {
            return null;
        }
        StructuralType declStructuralType = declType.getStructuralType(ctx);

        DeclType applyDecl = declStructuralType.findDecl(Util.APPLY_NAME, ctx);

        if (applyDecl == null || !(applyDecl instanceof DefDeclType)) {
            //TODO: will replace with ToolError in the future
            throw new RuntimeException("the declType is not a lambda type(it has no apply method)");
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
            //TODO: will replace with ToolError in the future
            throw new RuntimeException("the declType is not a lambda type(it has no apply method)");
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
}
