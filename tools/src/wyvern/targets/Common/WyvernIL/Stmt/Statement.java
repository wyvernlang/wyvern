package wyvern.targets.Common.WyvernIL.Stmt;

import wyvern.targets.Common.WyvernIL.visitor.StatementVisitor;

public interface Statement {
	public <R> R accept(StatementVisitor<R> visitor);
}
