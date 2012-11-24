package wyvern.tools.typedAST.extensions;

import wyvern.tools.typedAST.AbstractTypedAST;
import wyvern.tools.typedAST.Application;
import wyvern.tools.typedAST.ApplyableValue;
import wyvern.tools.typedAST.Value;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.util.TreeWriter;

public class ExternalFunction extends AbstractTypedAST implements ApplyableValue {
	private Type type;
	private Executor exec;
	
	public ExternalFunction(Type type, Executor exec) {
		this.type = type;
		this.exec = exec;
	}

	@Override
	public Type getType() {
		return type;
	}

	@Override
	public void writeArgsToTree(TreeWriter writer) {
		// no arguments at the moment; also not intended for serialization		
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
	public Value evaluateApplication(Application app, Environment env) {
		Value argValue = app.getArgument().evaluate(env);
		return exec.execute(argValue);
	}

}
