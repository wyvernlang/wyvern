package wyvern.tools.typedAST.extensions;

import wyvern.tools.typedAST.AbstractTypedAST;
import wyvern.tools.typedAST.AbstractValue;
import wyvern.tools.typedAST.Application;
import wyvern.tools.typedAST.ApplyableValue;
import wyvern.tools.typedAST.CoreAST;
import wyvern.tools.typedAST.CoreASTVisitor;
import wyvern.tools.typedAST.Value;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.util.TreeWriter;

public class ExternalFunction extends AbstractValue implements ApplyableValue, CoreAST {
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
	public Value evaluateApplication(Application app, Environment env) {
		Value argValue = app.getArgument().evaluate(env);
		return exec.execute(argValue);
	}

	@Override
	public void accept(CoreASTVisitor visitor) {
		// TODO  Not really sure what to do here.
	}

	private int line = -1;
	public int getLine() {
		return this.line; // TODO: NOT IMPLEMENTED YET.
	}
}
