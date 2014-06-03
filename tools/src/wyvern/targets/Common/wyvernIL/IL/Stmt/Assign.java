package wyvern.targets.Common.wyvernIL.IL.Stmt;

import wyvern.targets.Common.wyvernIL.IL.Expr.Expression;
import wyvern.targets.Common.wyvernIL.IL.visitor.StatementVisitor;

public class Assign implements Statement {

	private Expression dest;
	private Expression src;

	public Assign(Expression dest, Expression src) {
		this.dest = dest;
		this.src = src;
	}

	@Override
	public <R> R accept(StatementVisitor<R> visitor) {
		return visitor.visit(this);
	}

	public Expression getDest() {
		return dest;
	}

	public Expression getSrc() {
		return src;
	}

	@Override
	public String toString() {
		return dest + " = " + src;
	}
}
