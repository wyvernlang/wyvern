package wyvern.targets.Common.WyvernIL.Imm;

import wyvern.targets.Common.WyvernIL.visitor.OperandVisitor;

public class BoolValue implements Operand {

	private boolean value;

	public BoolValue(boolean value) {
		this.value = value;
	}

	@Override
	public <R> R accept(OperandVisitor<R> visitor) {
		return visitor.visit(this);
	}

	public boolean isValue() {
		return value;
	}
}
