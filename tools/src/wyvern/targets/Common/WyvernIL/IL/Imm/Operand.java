package wyvern.targets.Common.wyvernIL.IL.Imm;

import wyvern.targets.Common.wyvernIL.IL.visitor.OperandVisitor;

public interface Operand {
	public <R> R accept(OperandVisitor<R> visitor);
}
