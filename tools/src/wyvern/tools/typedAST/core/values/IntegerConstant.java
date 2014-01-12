package wyvern.tools.typedAST.core.values;

import wyvern.tools.errors.FileLocation;
import wyvern.tools.typedAST.abs.AbstractTypedAST;
import wyvern.tools.typedAST.abs.AbstractValue;
import wyvern.tools.typedAST.core.Invocation;
import wyvern.tools.typedAST.interfaces.*;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.Int;
import wyvern.tools.util.TreeWriter;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;

public class IntegerConstant extends AbstractValue implements InvokableValue, CoreAST {
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

	private FileLocation location = FileLocation.UNKNOWN;
	public FileLocation getLocation() {
		return this.location;
	}

	@Override
	public Map<String, TypedAST> getChildren() {
		Hashtable<String, TypedAST> children = new Hashtable<>();
		return children;
	}

	@Override
	public TypedAST cloneWithChildren(Map<String, TypedAST> nc) {
		return new IntegerConstant(value);
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof IntegerConstant))
			return false;
		if (((IntegerConstant) o).getValue() != this.getValue())
			return false;
		return true;
	}
}
