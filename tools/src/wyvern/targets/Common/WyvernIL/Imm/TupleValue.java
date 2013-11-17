package wyvern.targets.Common.WyvernIL.Imm;

import wyvern.targets.Common.WyvernIL.visitor.OperandVisitor;

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
}
