package wyvern.targets.Common.wyvernIL.IL.Stmt;

import wyvern.targets.Common.wyvernIL.IL.Imm.Operand;
import wyvern.targets.Common.wyvernIL.IL.visitor.StatementVisitor;

public class Return implements Statement {

	private Operand exn;

	public Return(Operand exn) {
		this.exn = exn;
	}

	@Override
	public <R> R accept(StatementVisitor<R> visitor) {
		return visitor.visit(this);
	}

	public Operand getExn() {
		return exn;
	}

	@Override
	public String toString() {
		return "return "+exn;
	}
}
