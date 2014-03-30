package wyvern.targets.Common.wyvernIL.IL.Imm;

import wyvern.targets.Common.wyvernIL.IL.visitor.OperandVisitor;

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
	@Override
	public String toString() {
		return ""+value;
	}
}
