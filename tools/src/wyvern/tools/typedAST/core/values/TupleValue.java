package wyvern.tools.typedAST.core.values;

import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.ToolError;
import wyvern.tools.parsing.LineParser;
import wyvern.tools.parsing.LineSequenceParser;
import wyvern.tools.typedAST.abs.AbstractValue;
import wyvern.tools.typedAST.core.Invocation;
import wyvern.tools.typedAST.interfaces.InvokableValue;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.Tuple;
import wyvern.tools.util.TreeWriter;

public class TupleValue extends AbstractValue implements InvokableValue {
	private Tuple tuple;
	private Value[] values;

	public TupleValue(Tuple tuple, Value[] values) {
		this.tuple = tuple;
		this.values = values;
	}

	@Override
	public Type getType() {
		return tuple;
	}

	@Override
	public void writeArgsToTree(TreeWriter writer) {
		writer.writeArgs(values);
	}
	
	public Value getValue(int index) {
		return values[index];
	}

	public Value[] getValues() {
		return values;
	}

	private FileLocation location = FileLocation.UNKNOWN;
	public FileLocation getLocation() {
		return this.location;
	}

	@Override
	public Value evaluateInvocation(Invocation exp, Environment env) {
		String name = exp.getOperationName();
		if (name.length() < 2)
			ToolError.reportError(ErrorMessage.CANNOT_INVOKE, exp.getLocation());
		if (!name.startsWith("n"))
			ToolError.reportError(ErrorMessage.CANNOT_INVOKE, exp.getLocation());
		int num = Integer.valueOf(name.substring(1));
		if (num >= values.length)
			ToolError.reportError(ErrorMessage.CANNOT_INVOKE, exp.getLocation());
		return values[num];
	}
}
