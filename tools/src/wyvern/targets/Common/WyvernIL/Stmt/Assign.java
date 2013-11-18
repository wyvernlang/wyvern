package wyvern.targets.Common.WyvernIL.Stmt;

import wyvern.targets.Common.WyvernIL.Expr.Expression;
import wyvern.targets.Common.WyvernIL.Imm.Operand;
import wyvern.targets.Common.WyvernIL.Imm.VarRef;
import wyvern.targets.Common.WyvernIL.visitor.StatementVisitor;

public class Assign implements Statement {

	private Operand dest;
	private Expression src;

	public Assign(Operand dest, Expression src) {
		this.dest = dest;
		this.src = src;
	}

	@Override
	public <R> R accept(StatementVisitor<R> visitor) {
		return visitor.visit(this);
	}

	public Operand getDest() {
		return dest;
	}

	public Expression getSrc() {
		return src;
	}
}
