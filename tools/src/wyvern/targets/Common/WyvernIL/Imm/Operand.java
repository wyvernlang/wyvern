package wyvern.targets.Common.WyvernIL.Imm;

import wyvern.targets.Common.WyvernIL.visitor.OperandVisitor;

public interface Operand {
	public <R> R accept(OperandVisitor<R> visitor);
}
