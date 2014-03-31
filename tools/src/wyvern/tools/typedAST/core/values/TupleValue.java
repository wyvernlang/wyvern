package wyvern.tools.typedAST.core.values;

import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.ToolError;
import wyvern.tools.typedAST.abs.AbstractValue;
import wyvern.tools.typedAST.core.expressions.Invocation;
import wyvern.tools.typedAST.interfaces.InvokableValue;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.Tuple;
import wyvern.tools.util.TreeWriter;

import java.util.HashMap;
import java.util.Map;

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
	public Map<String, TypedAST> getChildren() {
		Map<String, TypedAST> map = new HashMap<>();
		for (int i = 0; i < values.length; i++) {
			map.put(i + "", values[i]);
		}
		return map;
	}

	@Override
	public TypedAST cloneWithChildren(Map<String, TypedAST> newChildren) {
		Value[] ast = new Value[newChildren.size()];
		for (String k : newChildren.keySet()) {
			ast[Integer.parseInt(k)] = (Value)newChildren.get(k);
		}
		return new TupleValue(tuple, ast);
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
