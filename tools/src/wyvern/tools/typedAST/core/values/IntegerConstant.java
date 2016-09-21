package wyvern.tools.typedAST.core.values;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.expression.IntegerLiteral;
import wyvern.target.corewyvernIL.modules.TypedModuleSpec;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.typedAST.abs.AbstractValue;
import wyvern.tools.typedAST.core.expressions.Invocation;
import wyvern.tools.typedAST.interfaces.CoreAST;
import wyvern.tools.typedAST.interfaces.InvokableValue;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.typedAST.transformers.GenerationEnvironment;
import wyvern.tools.typedAST.transformers.ILWriter;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.Int;
import wyvern.tools.util.EvaluationEnvironment;
import wyvern.tools.util.TreeWriter;

public class IntegerConstant extends AbstractValue implements InvokableValue, CoreAST {
	private int value;
	
	@Deprecated
	public IntegerConstant(int i) {
		this(i, FileLocation.UNKNOWN);
	}

	public IntegerConstant(int i, FileLocation loc) {
		value = i;
		location = loc;
	}

	@Override
	public Type getType() {
		return new Int();
	}

	public int getValue() {
		return value;
	}

	@Override
	public Value evaluateInvocation(Invocation exp, EvaluationEnvironment env) {
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
				case "/": try { return new IntegerConstant(value / intArgValue.value); } catch (ArithmeticException e) { throw new RuntimeException(exp.getLocation() + "", e); }
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
		return new IntegerConstant(value, location);
	}

    @Override
    public void codegenToIL(GenerationEnvironment environment, ILWriter writer) {
        writer.write(new IntegerLiteral(value, location));
    }

    @Override
	public boolean equals(Object o) {
		if (!(o instanceof IntegerConstant))
			return false;
		if (((IntegerConstant) o).getValue() != this.getValue())
			return false;
		return true;
	}

	@Override
	public Expression generateIL(GenContext ctx, ValueType expectedType, List<TypedModuleSpec> dependencies) {
		return new IntegerLiteral(value, location);
	}
}
