package wyvern.tools.typedAST.extensions.values;

import wyvern.tools.parsing.LineParser;
import wyvern.tools.parsing.LineSequenceParser;
import wyvern.tools.typedAST.AbstractValue;
import wyvern.tools.typedAST.Value;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.Tuple;
import wyvern.tools.util.TreeWriter;

public class TupleValue extends AbstractValue {
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
	
}
