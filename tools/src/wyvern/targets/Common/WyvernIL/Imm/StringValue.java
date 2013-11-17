package wyvern.targets.Common.WyvernIL.Imm;

import wyvern.targets.Common.WyvernIL.visitor.OperandVisitor;

public class StringValue implements Operand {

	private String value;

	public StringValue(String value) {
		this.value = value;
	}

	@Override
	public <R> R accept(OperandVisitor<R> visitor) {
		return visitor.visit(this);
	}

	public String isValue() {
		return value;
	}
}
