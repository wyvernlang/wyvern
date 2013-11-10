package wyvern.targets.Common.WyvernIL.Expr;

import wyvern.targets.Common.WyvernIL.Imm.Operand;
import wyvern.targets.Common.WyvernIL.visitor.ExprVisitor;

public class Immediate implements Expression {

	private Operand inner;

	public Immediate(Operand inner) {
		this.inner = inner;
	}

	@Override
	public <R> R accept(ExprVisitor<R> visitor) {
		return visitor.visit(this);
	}

	public Operand getInner() {
		return inner;
	}
}
