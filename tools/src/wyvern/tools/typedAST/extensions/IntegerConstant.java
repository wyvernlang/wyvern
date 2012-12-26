package wyvern.tools.typedAST.extensions;

import wyvern.tools.typedAST.AbstractTypedAST;
import wyvern.tools.typedAST.CoreAST;
import wyvern.tools.typedAST.CoreASTVisitor;
import wyvern.tools.typedAST.InvokableValue;
import wyvern.tools.typedAST.Invocation;
import wyvern.tools.typedAST.Value;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.Int;
import wyvern.tools.util.TreeWriter;

public class IntegerConstant extends AbstractTypedAST implements InvokableValue, CoreAST {
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
		IntegerConstant intArgValue = null;
		String operator = exp.getOperationName();

		Value argValue = exp.getArgument().evaluate(env);
		if (argValue instanceof StringConstant) {		// int + "str"
			if (operator.equals("+")) {
				return new StringConstant(this.value + ((StringConstant) argValue).getValue());
			} else {
				throw new RuntimeException("forgot to typecheck!");
			}
		} else if (argValue instanceof IntegerConstant) {		//int op int
			intArgValue = (IntegerConstant)argValue;
			switch(operator) {
				case "+": return new IntegerConstant(value + intArgValue.value);
				case "-": return new IntegerConstant(value - intArgValue.value);
				case "*": return new IntegerConstant(value * intArgValue.value);
				case "/": return new IntegerConstant(value / intArgValue.value);
				case ">": return new BooleanConstant(value > intArgValue.value);
				case "<": return new BooleanConstant(value < intArgValue.value);
				case ">=": return new BooleanConstant(value >= intArgValue.value);
				case "<=": return new BooleanConstant(value <= intArgValue.value);
				case "==": return new BooleanConstant(value == intArgValue.value);
				case "!=": return new BooleanConstant(value != intArgValue.value);
				default: throw new RuntimeException("forgot to typecheck!");
			}
		} else {
//			shouldn't get here
			throw new RuntimeException("forgot to typecheck!");
		}
	}

	@Override
	public void accept(CoreASTVisitor visitor) {
		visitor.visit(this);
	}
}
