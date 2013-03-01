package wyvern.tools.typedAST.extensions.values;

import wyvern.tools.typedAST.AbstractTypedAST;
import wyvern.tools.typedAST.AbstractValue;
import wyvern.tools.typedAST.CoreAST;
import wyvern.tools.typedAST.CoreASTVisitor;
import wyvern.tools.typedAST.Invocation;
import wyvern.tools.typedAST.InvokableValue;
import wyvern.tools.typedAST.Value;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.Bool;
import wyvern.tools.util.TreeWriter;

public class BooleanConstant extends AbstractValue implements InvokableValue, CoreAST {
	private boolean value;
	
	public BooleanConstant(boolean b) {
		this.value = b;
	}

	@Override
	public Type getType() {
		return Bool.getInstance();
	}

	@Override
	public void writeArgsToTree(TreeWriter writer) {
		writer.writeArgs(this.value);
	}

	public boolean getValue() {
		return this.value;
	}

	@Override
	public Value evaluateInvocation(Invocation exp, Environment env) {
		BooleanConstant argValue = (BooleanConstant) exp.getArgument().evaluate(env);
		String operator = exp.getOperationName();
		switch(operator) {
			case "&&": return new BooleanConstant(value && argValue.value);
			case "||": return new BooleanConstant(value || argValue.value);
			default: throw new RuntimeException("forgot to typecheck!");
		}
	}

	@Override
	public void accept(CoreASTVisitor visitor) {
		visitor.visit(this);
	}

	private int line = -1;
	public int getLine() {
		return this.line; // TODO: NOT IMPLEMENTED YET.
	}
}
