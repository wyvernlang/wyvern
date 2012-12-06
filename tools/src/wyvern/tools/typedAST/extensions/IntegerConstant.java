package wyvern.tools.typedAST.extensions;

import wyvern.tools.typedAST.AbstractTypedAST;
import wyvern.tools.typedAST.InvokableValue;
import wyvern.tools.typedAST.Invocation;
import wyvern.tools.typedAST.Value;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.Int;
import wyvern.tools.util.TreeWriter;

public class IntegerConstant extends AbstractTypedAST implements InvokableValue {
	private int value;
	
	public IntegerConstant(int i) {
		value = i;
	}

	@Override
	public Type getType() {
		return Int.getInstance();
	}
	
	@Override
	public void writeArgsToTree(TreeWriter writer) {
		writer.writeArgs(value);		
	}

	@Override
	public Type typecheck() {
		return getType();
	}

	@Override
	public Value evaluate(Environment env) {
		return this;
	}
	
	public int getValue() {
		return value;
	}

	@Override
	public Value evaluateInvocation(Invocation exp, Environment env) {
		IntegerConstant argValue = (IntegerConstant) exp.getArgument().evaluate(env);
		String operator = exp.getOperationName();
		switch(operator) {
			case "+": return new IntegerConstant(value + argValue.value);
			case "-": return new IntegerConstant(value - argValue.value);
			case "*": return new IntegerConstant(value * argValue.value);
			case "/": return new IntegerConstant(value / argValue.value);
			case ">": return new BooleanConstant(value > argValue.value);
			case "<": return new BooleanConstant(value < argValue.value);
			case ">=": return new BooleanConstant(value >= argValue.value);
			case "<=": return new BooleanConstant(value <= argValue.value);
			case "==": return new BooleanConstant(value == argValue.value);
			case "!=": return new BooleanConstant(value != argValue.value);
			default: throw new RuntimeException("forgot to typecheck!");
		}
	}
}
