package wyvern.tools.typedAST.extensions.values;

import wyvern.tools.typedAST.AbstractValue;
import wyvern.tools.typedAST.Value;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.Tuple;
import wyvern.tools.util.TreeWriter;

public class VarValue extends AbstractValue {
	private Value innerValue;

	public VarValue(Value initial) {
		this.innerValue = initial;
	}
	
	public void setValue(Value newV) {
		if (innerValue.getType() != newV.getType())
			throw new RuntimeException("Typecheck error causing problems at runtime");
		innerValue = newV;
	}

	@Override
	public Type getType() {
		return innerValue.getType();
	}

	@Override
	public void writeArgsToTree(TreeWriter writer) {
		writer.writeArgs(innerValue);
	}
	
	public Value getValue() {
		return innerValue;
	}

	private int line = -1;
	public int getLine() {
		return this.line; // TODO: NOT IMPLEMENTED YET.
	}
}
