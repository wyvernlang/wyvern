package wyvern.targets.Common.WyvernIL.Imm;

import wyvern.targets.Common.WyvernIL.visitor.OperandVisitor;

import java.util.LinkedList;
import java.util.List;

public class TupleValue implements Operand {

	private List<Operand> operands;

	public TupleValue(List<Operand> operands) {
		this.operands = operands;
	}

	@Override
	public <R> R accept(OperandVisitor<R> visitor) {
		return visitor.visit(this);
	}

	public List<Operand> getOperands() {
		return operands;
	}
	private static String join(List<String> list, String delim) {

		StringBuilder sb = new StringBuilder();

		String loopDelim = "";

		for(String s : list) {

			sb.append(loopDelim);
			sb.append(s);

			loopDelim = delim;
		}

		return sb.toString();
	}
	@Override
	public String toString() {
		List<String> sb = new LinkedList<>();
		for (Operand op : operands) {
			sb.add(op.toString());
		}

		return "("+join(sb,",") +")";
	}
}
