package wyvern.tools.typedAST.core.expressions;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import wyvern.target.corewyvernIL.FormalArg;
import wyvern.target.corewyvernIL.decl.Declaration;
import wyvern.target.corewyvernIL.decl.TypeDeclaration;
import wyvern.target.corewyvernIL.decltype.ConcreteTypeMember;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.decltype.DefDeclType;
import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.expression.IExpr;
import wyvern.target.corewyvernIL.expression.MethodCall;
import wyvern.target.corewyvernIL.expression.New;
import wyvern.target.corewyvernIL.modules.TypedModuleSpec;
import wyvern.target.corewyvernIL.support.CallableExprGenerator;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.type.StructuralType;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.ToolError;
import wyvern.tools.typedAST.abs.AbstractExpressionAST;
import wyvern.tools.typedAST.core.declarations.DefDeclaration;
import wyvern.tools.typedAST.interfaces.CoreAST;
import wyvern.tools.typedAST.interfaces.ExpressionAST;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.types.Type;

public class Application extends AbstractExpressionAST implements CoreAST {
    private ExpressionAST function;
    private List<TypedAST> arguments;
    private List<Type> generics;
    private FileLocation location;

    /**
     * Application represents a call cite for a function call.
     *
     * @param function the function that is called
     * @param arguments the arguments passed at the call site
     * @param location the location of the call site in the source file
     * @param generics2 the vector of type parameters passed at the call site
     */
    public Application(TypedAST function, List<TypedAST> arguments,
            FileLocation location, List<Type> generics2) {
        this.function = (ExpressionAST) function;
        this.arguments = arguments == null ? new LinkedList<TypedAST>() : arguments;
        this.location = location;
        this.generics = (generics2 != null) ? generics2 : new LinkedList<Type>();
    }

    public List<TypedAST> getArguments() {
        return arguments;
    }

    public TypedAST getFunction() {
        return function;
    }

    public List<Type> getGenerics() {
        return generics;
    }

    public FileLocation getLocation() {
        return this.location;
    }

    @Override
    public IExpr generateIL(
            GenContext ctx,
            ValueType expectedType,
            List<TypedModuleSpec> dependencies) {

        CallableExprGenerator exprGen = function.getCallableExpr(ctx);

        /* Method call on a dynamic object. We pretend there's an appropriate declaration,
         * and ignore the expression generator. */
        if (exprGen.getDeclType(ctx) == null) {

            // Generate code for the arguments.
            List<IExpr> args = new LinkedList<>();
            for (TypedAST a : arguments) {
                args.add(((ExpressionAST) a).generateIL(ctx, null, dependencies));
            }

            // Need to do this to find out what the method name is.
            if (!(function instanceof Invocation)) {
                throw new RuntimeException("Getting field of dynamic object,"
                        + "which isn't an invocation.");
            }
            Invocation invocation = (Invocation) function;

            return new MethodCall(
                    invocation.getReceiver().generateIL(ctx, null, dependencies),
                    invocation.getOperationName(),
                    args,
                    this);
        }

        /* Otherwise look up declaration. Ensure arguments match the declaration. */
        DefDeclType ddt = exprGen.getDeclType(ctx);
        List<FormalArg> formals = ddt.getFormalArgs();
        List<IExpr> args = new LinkedList<IExpr>();

        // Add generic arguments to the argslist
        generateGenericArgs(ddt.getName(), args, formals, ctx, ddt, dependencies);

        generateILForTuples(formals, args, ctx, dependencies);

        // generate the call
        return exprGen.genExprWithArgs(args, this);
    }

    private int countFormalGenerics(List<FormalArg> formals) {

        int count = 0;
        for (FormalArg formal : formals) {
            String name = formal.getName();
            if (!name.startsWith(DefDeclaration.GENERIC_PREFIX)) {
                // We're hit the end of the generic args!
                break;
            }
            count++;
        }
        return count;
    }

    private void addGenericToArgList(
            String formalName,
            Type generic,
            List<IExpr> args,
            GenContext ctx
            ) {

        String genericName = formalName
                .substring(DefDeclaration.GENERIC_PREFIX.length());

        ValueType vt = generic.getILType(ctx);
        args.add(
                new wyvern.target.corewyvernIL.expression.New(
                        new TypeDeclaration(genericName, vt, this.location)
                        )
                );
    }

    private void generateILForTuples(
            List<FormalArg> formals,
            List<IExpr> args,
            GenContext ctx,
            List<TypedModuleSpec> dependencies
            ) {

        List<TypedAST> rawArgs = arguments;
        if (formals.size() != rawArgs.size() + args.size()) {
            ToolError.reportError(
                    ErrorMessage.WRONG_NUMBER_OF_ARGUMENTS,
                    this,
                    "" + formals.size(),
                    "" + (rawArgs.size() + args.size())
                    );
        }
        for (int i = 0; i < rawArgs.size(); i++) {
            ValueType expectedArgType = formals.get(i + this.generics.size()).getType();
            ExpressionAST ast = (ExpressionAST) rawArgs.get(i);
            // TODO: propagate types downward from formals
            args.add(ast.generateIL(ctx, expectedArgType, dependencies));
        }
    }

    private void generateGenericArgs(
            String methodName,
            List<IExpr> args,
            List<FormalArg> formals,
            GenContext ctx,
            DefDeclType ddt,
            List<TypedModuleSpec> deps
            ) {
        int count = countFormalGenerics(formals);
        if (count < this.generics.size()) {
            // then the number of actual generics is greater than the number of formal generics
            // this is not permitted.
            ToolError.reportError(ErrorMessage.EXTRA_GENERICS_AT_CALL_SITE, this);
        } else if (count == this.generics.size()) {
            // then we can simply add each of the actual generics to the argument's list
            for (int i = 0; i < count; i++) {
                String formalName = formals.get(i).getName();
                Type generic = this.generics.get(i);
                addGenericToArgList(formalName, generic, args, ctx);
            }
        } else {
            // this case executes when count > this.generics.size()
            // In this case, we can do type inference to determine what types have been elided
            inferGenericArgs(methodName, args, formals, ctx, ddt, deps);
        }
    }

    private void inferGenericArgs(
            String methodName,
            List<IExpr> args,
            List<FormalArg> formals,
            GenContext ctx,
            DefDeclType ddt,
            List<TypedModuleSpec> deps
            ) {
        // First, add any of the pre-existing generics to the argument list.
        addExistingGenerics(args, formals, ctx);

        // Now, try to infer the type of the remaining generics.

        // Collect the mapping from generic args to provided args
        Map<Integer, List<Integer>> inferenceMap = ddt.genericMapping();
        int count = countFormalGenerics(formals);

        for (int i = this.generics.size(); i < count; i++) {

            if (!inferenceMap.containsKey(i)) {
                // then we can't infer the type
                ToolError.reportError(ErrorMessage.MISSING_GENERICS_AT_CALL_SITE, this, methodName);
            }

            List<Integer> positions = inferenceMap.get(i);
            if (positions.isEmpty()) {
                ToolError.reportError(ErrorMessage.CANNOT_INFER_GENERIC, this);
            }
            // formal position tells you where in
            // the formals the argument that uses the generic is
            int formalPos = positions.get(0);

            // actual position tells you where in the actual
            // argument list the type should be
            int actualPos = formalPos - count;

            IExpr inferArg = ((ExpressionAST) arguments.get(actualPos)).generateIL(ctx, null, deps);
            this.addInferredType(args, formals, ctx, inferArg.typeCheck(ctx, null), i);
        }
    }

    private void addInferredType(
            List<IExpr> args,
            List<FormalArg> formals,
            GenContext ctx,
            ValueType inferredType,
            int formalIndex
            ) {
        List<Declaration> members = new LinkedList<>();
        TypeDeclaration typeMember = new TypeDeclaration(
                formals.get(formalIndex).getName()
                .substring(
                        DefDeclaration.GENERIC_PREFIX.length()),
                inferredType,
                null
                );
        members.add(typeMember);
        List<DeclType> declTypes = new LinkedList<DeclType>();
        declTypes.add(
                new ConcreteTypeMember(
                        formals.get(formalIndex).getName()
                        .substring(DefDeclaration.GENERIC_PREFIX.length()),
                        inferredType)
                );
        ValueType actualArgType = new StructuralType("self", declTypes);
        Expression newExp = new New(members, "self", actualArgType, null);
        args.add(newExp);
    }

    private void addExistingGenerics(
            List<IExpr> args,
            List<FormalArg> formals,
            GenContext ctx
            ) {
        for (int i = 0; i < this.generics.size(); i++) {
            String formalName = formals.get(i).getName();
            Type generic = this.generics.get(i);
            addGenericToArgList(formalName, generic, args, ctx);
        }
    }

    @Override
    public StringBuilder prettyPrint() {
        StringBuilder sb = new StringBuilder();
        sb.append("Application(function=");
        sb.append(function.prettyPrint());
        sb.append(", argument=");
        sb.append(arguments.toString());
        sb.append(")");
        return sb;
    }

    /** Side-effects the Application to add an argument;
     * used when we parse an argument separately from the application itself.
     */
    public void addArgument(TypedAST argument) {
        LinkedList<TypedAST> args = new LinkedList<TypedAST>(arguments);
        args.addLast(argument);
        arguments = args;
    }

}
