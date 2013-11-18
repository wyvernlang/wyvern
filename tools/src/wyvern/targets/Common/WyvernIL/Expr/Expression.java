package wyvern.targets.Common.WyvernIL.Expr;

import wyvern.targets.Common.WyvernIL.visitor.ExprVisitor;

public interface Expression {
	public <R> R accept(ExprVisitor<R> visitor);
}
