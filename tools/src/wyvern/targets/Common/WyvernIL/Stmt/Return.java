package wyvern.targets.Common.WyvernIL.Stmt;

import wyvern.targets.Common.WyvernIL.Imm.Operand;
import wyvern.targets.Common.WyvernIL.visitor.StatementVisitor;

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
}
