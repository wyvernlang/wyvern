package wyvern.tools.typedAST.core.expressions;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import wyvern.stdlib.Globals;
import wyvern.target.corewyvernIL.FormalArg;
import wyvern.target.corewyvernIL.decl.Declaration;
import wyvern.target.corewyvernIL.decl.TypeDeclaration;
import wyvern.target.corewyvernIL.decltype.ConcreteTypeMember;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.decltype.DefDeclType;
import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.expression.IExpr;
import wyvern.target.corewyvernIL.expression.New;
import wyvern.target.corewyvernIL.modules.TypedModuleSpec;
import wyvern.target.corewyvernIL.support.CallableExprGenerator;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.type.StructuralType;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.ErrorMessage;
import static wyvern.tools.errors.ErrorMessage.TYPE_CANNOT_BE_APPLIED;
import static wyvern.tools.errors.ErrorMessage.VALUE_CANNOT_BE_APPLIED;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.ToolError;
import static wyvern.tools.errors.ToolError.reportError;
import static wyvern.tools.errors.ToolError.reportEvalError;
import wyvern.tools.typedAST.abs.CachingTypedAST;
import wyvern.tools.typedAST.core.declarations.DefDeclaration;
import wyvern.tools.typedAST.core.values.UnitVal;
import wyvern.tools.typedAST.interfaces.ApplyableValue;
import wyvern.tools.typedAST.interfaces.CoreAST;
import wyvern.tools.typedAST.interfaces.ExpressionAST;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.types.ApplyableType;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.Arrow;
import wyvern.tools.types.extensions.Intersection;
import wyvern.tools.util.EvaluationEnvironment;

public class Application extends CachingTypedAST implements CoreAST {
    private ExpressionAST function;
    private ExpressionAST argument;
    private List<String> generics;
    private FileLocation location;

    public Application(TypedAST function, TypedAST argument, FileLocation location) {
        this(function, argument, location, null);
    }

    /**
      * Application represents a call cite for a function call.
      *
      * @param function the function that is called
      * @param argument the argument passed at the call site (may be a tuple, unit, or singleton
      * @param location the location of the call site in the source file
      * @param generics the vector of type parameters passed at the call site
      */
    public Application(TypedAST function, TypedAST argument,
            FileLocation location, List<String> generics) {
        this.function = (ExpressionAST) function;
        this.argument = (ExpressionAST) argument;
        this.location = location;
        this.generics = (generics != null) ? generics : new LinkedList<String>();
    }

    @Override
    protected Type doTypecheck(Environment env, Optional<Type> expected) {
        Type fnType = function.typecheck(env, Optional.empty());

        Type argument = null;
        if (fnType instanceof Arrow) {
            argument = ((Arrow) fnType).getArgument();
        } else if (fnType instanceof Intersection) {
            List<Type> args = fnType.getChildren().values().stream()
                    .filter(tpe -> tpe instanceof Arrow).map(tpe -> ((Arrow)tpe).getArgument())
                    .collect(Collectors.toList());
            argument = new Intersection(args);
        }
        if (this.argument != null) {
            this.argument.typecheck(env, Optional.ofNullable(argument));
        }

        if (!(fnType instanceof ApplyableType)) {
            reportError(TYPE_CANNOT_BE_APPLIED, this, fnType.toString());
        }

        return ((ApplyableType) fnType).checkApplication(this, env);
    }

    public TypedAST getArgument() {
        return argument;
    }

    public TypedAST getFunction() {
        return function;
    }

    @Override
    public Value evaluate(EvaluationEnvironment env) {
        TypedAST lhs = function.evaluate(env);
        if (Globals.checkRuntimeTypes && !(lhs instanceof ApplyableValue)) {
            reportEvalError(VALUE_CANNOT_BE_APPLIED, lhs.toString(), this);
        }
        ApplyableValue fnValue = (ApplyableValue) lhs;

        return fnValue.evaluateApplication(this, env);
    }

    @Override
    public Map<String, TypedAST> getChildren() {
        Hashtable<String, TypedAST> children = new Hashtable<>();
        children.put("function", function);
        children.put("argument", argument);
        return children;
    }

    @Override
    public ExpressionAST doClone(Map<String, TypedAST> nc) {
        return new Application(
            (ExpressionAST) nc.get("function"),
            (ExpressionAST) nc.get("argument"),
            location
        );
    }

    public FileLocation getLocation() {
        return this.location;
    }

    private ValueType getILTypeForGeneric(GenContext ctx, String genericName) {
        return ctx.lookupType(genericName, this.getLocation());
    }

    @Override
    public IExpr generateIL(
            GenContext ctx,
            ValueType expectedType,
            List<TypedModuleSpec> dependencies) {

        CallableExprGenerator exprGen = function.getCallableExpr(ctx);
        DefDeclType ddt = exprGen.getDeclType(ctx);
        List<FormalArg> formals = ddt.getFormalArgs();

        // generate arguments       
        List<IExpr> args = new LinkedList<IExpr>();

        // Add generic arguments to the argslist
        generateGenericArgs(args, formals, ctx, ddt, dependencies);

        if (argument instanceof TupleObject) {
            generateILForTuples(formals, args, ctx, dependencies);
        } else if (argument instanceof UnitVal) {
            // leave args empty
        } else {
            if (formals.size() != 1 + args.size()) {
                ToolError.reportError(
                    ErrorMessage.WRONG_NUMBER_OF_ARGUMENTS,
                    this,
                    "" + formals.size()
                );
            }

            // TODO: propagate types downward from formals
            args.add(argument.generateIL(ctx, formals.get(0).getType(), null));
        }

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
          String generic,
          List<IExpr> args,
          GenContext ctx
    ) {
        
        String genericName = formalName
            .substring(DefDeclaration.GENERIC_PREFIX.length());

        ValueType vt = getILTypeForGeneric(ctx, generic);
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
        
        ExpressionAST[] rawArgs = ((TupleObject) this.argument).getObjects();
        if (formals.size() != rawArgs.length + this.generics.size()) {
            ToolError.reportError(
                ErrorMessage.WRONG_NUMBER_OF_ARGUMENTS,
                this,
                "" + formals.size()
            );
        }
        for (int i = 0; i < rawArgs.length; i++) {
            ValueType expectedArgType = formals.get(i + this.generics.size()).getType();
            ExpressionAST ast = rawArgs[i];
            // TODO: propagate types downward from formals
            args.add(ast.generateIL(ctx, expectedArgType, dependencies));
        }
    }

    private void generateGenericArgs(
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
                String generic = this.generics.get(i);
                addGenericToArgList(formalName, generic, args, ctx);    
            }
        } else {
            // this case executes when count > this.generics.size()
            // In this case, we can do type inference to determine what types have been elided
            inferGenericArgs(args, formals, ctx, ddt, deps);
        }
    }

    private void inferGenericArgs(
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
                // TODO Missing generic at call site
                ToolError.reportError(ErrorMessage.EXTRA_GENERICS_AT_CALL_SITE, this);
            }

            // formal position tells you where in the formals the argument that uses the generic is
            List<Integer> positions = inferenceMap.get(i);
            int formalPos = positions.get(0);
            // actual position tells you where in the actual argument list the type should be
            int actualPos = formalPos - count;
            if (this.argument instanceof TupleObject) {
                ExpressionAST[] rawArgs = ((TupleObject) this.argument).getObjects();
                if (formalPos == rawArgs.length) {
                    // then we're inferring from the result type
                    throw new UnsupportedOperationException(
                        "Can't infer the result type yet."
                    );

                } else {
                    ExpressionAST inferArg = rawArgs[formalPos];
                    args.add(inferArg.generateIL(
                        ctx, formals.get(formalPos).getType(), deps
                    ));
                }
            } else if (this.argument instanceof UnitVal) {
                // uhhhhh?
                // The arg is a unit value. We must be inferring from the result type
                throw new UnsupportedOperationException(
                    "Can't infer the result type yet.");

            } else {
            
                // Then the arg must be a single element
                if (actualPos != 0) {
                    // Inferring from a formal arg that doesn't exist
                    // TODO unless we're inferring from the result type.....
                    // ToolError
                    throw new UnsupportedOperationException(
                        "Can't infer the result type yet.");
                }

                // Now we know that the argument is the inferrable type.
                // TODO Make this understandable @Robbie
                final IExpr argIL = this.argument
                    .generateIL(ctx, null, deps);
                ValueType inferredType = argIL.typeCheck(ctx);
                List<Declaration> members = new LinkedList<>();
                TypeDeclaration typeMember = new TypeDeclaration(
                        formals.get(0).getName()
                            .substring(
                                DefDeclaration.GENERIC_PREFIX.length()),
                            inferredType, 
                            null
                );
                members.add(typeMember);
                List<DeclType> declTypes = new LinkedList<DeclType>();
                declTypes.add(
                    new ConcreteTypeMember(
                        formals.get(0).getName()
                            .substring(DefDeclaration.GENERIC_PREFIX.length()),
                        inferredType)
                );
                ValueType actualArgType = new StructuralType("self", declTypes);
                Expression newExp = new New(members, "self", actualArgType, null);
                args.add(newExp);
            }
        }
    }

    private void addExistingGenerics(
            List<IExpr> args,
            List<FormalArg> formals,
            GenContext ctx
    ) {
        for (int i = 0; i < this.generics.size(); i++) {
            String formalName = formals.get(i).getName();
            String generic = this.generics.get(i);
            addGenericToArgList(formalName, generic, args, ctx);    
        }
    }
}
