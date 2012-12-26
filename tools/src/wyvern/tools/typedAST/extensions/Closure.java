package wyvern.tools.typedAST.extensions;

import wyvern.tools.typedAST.AbstractTypedAST;
import wyvern.tools.typedAST.Application;
import wyvern.tools.typedAST.ApplyableValue;
import wyvern.tools.typedAST.CoreAST;
import wyvern.tools.typedAST.CoreASTVisitor;
import wyvern.tools.typedAST.Value;
import wyvern.tools.typedAST.binding.ValueBinding;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.util.TreeWriter;

public class Closure extends AbstractTypedAST implements ApplyableValue, CoreAST {
	private Fn function;
	private Environment env;

	public Closure(Fn function, Environment env) {
		this.function = function;
		this.env = env;
	}

	@Override
	public Type getType() {
		return function.getType();
	}

	@Override
	public Type typecheck() {
		return getType();
	}

	@Override
	public Value evaluate(Environment env) {
		return this;
	}

	@Override
	public void writeArgsToTree(TreeWriter writer) {
		writer.writeArgs(function, env);
	}

	@Override
	public Value evaluateApplication(Application app, Environment argEnv) {
		Value argValue = app.getArgument().evaluate(argEnv);
		Environment bodyEnv = env.extend(new ValueBinding(function.getBinding().getName(), argValue));
		return function.getBody().evaluate(bodyEnv);
	}

	@Override
	public void accept(CoreASTVisitor visitor) {
		// TODO Think about this more. For now, just let function handle it
		function.accept(visitor);
	}

}
