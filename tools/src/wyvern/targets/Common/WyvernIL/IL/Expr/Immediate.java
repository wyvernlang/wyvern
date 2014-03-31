package wyvern.targets.Common.wyvernIL.IL.Expr;

import wyvern.targets.Common.wyvernIL.IL.Imm.Operand;
import wyvern.targets.Common.wyvernIL.IL.visitor.ExprVisitor;

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
	@Override
	public String toString() {
		return ""+inner;
	}
}
