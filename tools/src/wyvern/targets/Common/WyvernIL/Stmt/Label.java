package wyvern.targets.Common.WyvernIL.Stmt;

import wyvern.targets.Common.WyvernIL.visitor.StatementVisitor;

public class Label implements Statement {
	@Override
	public <R> R accept(StatementVisitor<R> visitor) {
		return visitor.visit(this);
	}
}
