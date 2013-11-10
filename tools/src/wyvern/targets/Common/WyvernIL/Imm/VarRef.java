package wyvern.targets.Common.WyvernIL.Imm;

import wyvern.targets.Common.WyvernIL.visitor.OperandVisitor;

public class VarRef implements Operand {
	@Override
	public <R> R accept(OperandVisitor<R> visitor) {
		return visitor.visit(this);
	}
}
