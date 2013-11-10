package wyvern.targets.Common.WyvernIL.Imm;

import wyvern.targets.Common.WyvernIL.visitor.OperandVisitor;

public class IntValue implements Operand {

	private int value;

	public IntValue(int value) {
		this.value = value;
	}

	@Override
	public <R> R accept(OperandVisitor<R> visitor) {
		return visitor.visit(this);
	}

	public int getValue() {
		return value;
	}
}
