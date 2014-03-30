package wyvern.targets.Common.wyvernIL.IL.Stmt;

import wyvern.targets.Common.wyvernIL.IL.visitor.StatementVisitor;

public interface Statement {
	public <R> R accept(StatementVisitor<R> visitor);
}
