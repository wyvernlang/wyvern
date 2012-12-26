package wyvern.tools.typedAST;

import static wyvern.tools.errors.ErrorMessage.VALUE_CANNOT_BE_APPLIED;
import static wyvern.tools.errors.ErrorMessage.TYPE_CANNOT_BE_APPLIED;
import wyvern.tools.types.ApplyableType;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.util.TreeWriter;
import static wyvern.tools.errors.ToolError.reportError;
import static wyvern.tools.errors.ToolError.reportEvalError;

public class Application extends CachingTypedAST implements CoreAST {
	private TypedAST function;
	private TypedAST argument;

	public Application(TypedAST function, TypedAST argument) {
		this.function = function;
		this.argument = argument;
	}

	@Override
	public void writeArgsToTree(TreeWriter writer) {
		writer.writeArgs(function, argument);
	}

	@Override
	protected Type doTypecheck() {
		Type fnType = function.typecheck();
		if (!(fnType instanceof ApplyableType))
			reportError(TYPE_CANNOT_BE_APPLIED, fnType.toString(), this);
		return ((ApplyableType)fnType).checkApplication(this);
	}

	public TypedAST getArgument() {
		return argument;
	}

	public TypedAST getFunction() {
		return function;
	}

	@Override
	public Value evaluate(Environment env) {
		TypedAST lhs = function.evaluate(env);
		if (!(lhs instanceof ApplyableValue))
			reportEvalError(VALUE_CANNOT_BE_APPLIED, lhs.toString(), this);
		ApplyableValue fnValue = (ApplyableValue) lhs;
		return fnValue.evaluateApplication(this, env);
	}

	@Override
	public void accept(CoreASTVisitor visitor) {
		if (argument instanceof CoreAST)
			((CoreAST) argument).accept(visitor);
		if (function instanceof CoreAST)
			((CoreAST) function).accept(visitor);
		visitor.visit(this);
	}

}
