package wyvern.tools.typedAST.core.expressions;

import wyvern.stdlib.Globals;
import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.expression.MethodCall;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.typedAST.abs.CachingTypedAST;
import wyvern.tools.typedAST.interfaces.*;
import wyvern.tools.typedAST.transformers.ExpressionWriter;
import wyvern.tools.typedAST.transformers.GenerationEnvironment;
import wyvern.tools.typedAST.transformers.ILWriter;
import wyvern.tools.types.ApplyableType;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.Arrow;
import wyvern.tools.types.extensions.Intersection;
import wyvern.tools.util.EvaluationEnvironment;
import wyvern.tools.util.TreeWriter;

import java.util.*;
import java.util.stream.Collectors;

import static wyvern.tools.errors.ErrorMessage.TYPE_CANNOT_BE_APPLIED;
import static wyvern.tools.errors.ErrorMessage.VALUE_CANNOT_BE_APPLIED;
import static wyvern.tools.errors.ToolError.reportError;
import static wyvern.tools.errors.ToolError.reportEvalError;

public class Application extends CachingTypedAST implements CoreAST {
	private TypedAST function;
	private TypedAST argument;

	public Application(TypedAST function, TypedAST argument, FileLocation location) {
		this.function = function;
		this.argument = argument;
		this.location = location;
	}

	@Override
	public void writeArgsToTree(TreeWriter writer) {
		writer.writeArgs(function, argument);
	}

	@Override
	protected Type doTypecheck(Environment env, Optional<Type> expected) {
		Type fnType = function.typecheck(env, Optional.empty());

		Type argument = null;
		if (fnType instanceof Arrow)
			argument = ((Arrow) fnType).getArgument();
		else if (fnType instanceof Intersection) {
			List<Type> args = fnType.getChildren().values().stream()
					.filter(tpe -> tpe instanceof Arrow).map(tpe->((Arrow)tpe).getArgument())
					.collect(Collectors.toList());
			argument = new Intersection(args);
		}
		if (this.argument != null)
			this.argument.typecheck(env, Optional.ofNullable(argument));
		
		if (!(fnType instanceof ApplyableType))
			reportError(TYPE_CANNOT_BE_APPLIED, this, fnType.toString());
		
		return ((ApplyableType)fnType).checkApplication(this, env);
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
		if (Globals.checkRuntimeTypes && !(lhs instanceof ApplyableValue))
			reportEvalError(VALUE_CANNOT_BE_APPLIED, lhs.toString(), this);
		ApplyableValue fnValue = (ApplyableValue) lhs;
		
		return fnValue.evaluateApplication(this, env);
	}

	@Override
	public void accept(CoreASTVisitor visitor) {
		visitor.visit(this);
	}
	@Override
	public Map<String, TypedAST> getChildren() {
		Hashtable<String, TypedAST> children = new Hashtable<>();
		children.put("function", function);
		children.put("argument", argument);
		return children;
	}

    @Override
    public void codegenToIL(GenerationEnvironment environment, ILWriter writer) {
        List<Expression> arguments;
        if (argument instanceof TupleObject) {
            arguments = Arrays.stream(((TupleObject) argument).getObjects()).map(a -> ExpressionWriter.generate(iw -> a.codegenToIL(environment, iw))).collect(Collectors.toList());
        } else {
            arguments = Arrays.asList(ExpressionWriter.generate(iw->argument.codegenToIL(environment, iw)));
        }
        if (function instanceof Invocation && ((Invocation) function).getArgument() == null) { // straight MethodCall
            writer.write(new MethodCall(ExpressionWriter.generate(iw -> function.codegenToIL(environment, iw)), ((Invocation) function).getOperationName(), arguments));
        }
        Expression expr = ExpressionWriter.generate(iw -> function.codegenToIL(environment, iw));
        writer.write(new MethodCall(expr, "call", arguments));
    }

    @Override
	public TypedAST doClone(Map<String, TypedAST> nc) {
		return new Application(nc.get("function"), nc.get("argument"), location);
	}

	private FileLocation location = FileLocation.UNKNOWN;
	public FileLocation getLocation() {
		return this.location;
	}

	@Override
	public Expression generateIL(GenContext ctx) {
		// generate arguments		
		List<Expression> args = new LinkedList<Expression>();
        if (argument instanceof TupleObject) {
        	for (TypedAST ast : ((TupleObject) argument).getObjects()) {
        		args.add(ast.generateIL(ctx));
        	}
        } else {
        	args.add(argument.generateIL(ctx));
        }
		
		// generate the call
		String methodName = null;
		Expression receiver = null;
		
		if (function instanceof Invocation) {
			Invocation i = (Invocation) function;
			methodName = i.getOperationName();
			receiver = i.getReceiver().generateIL(ctx);
		} else {
			throw new RuntimeException("calls with no receiver are not implemented");
		}
		
		return new MethodCall(receiver, methodName, args);
	}
}
