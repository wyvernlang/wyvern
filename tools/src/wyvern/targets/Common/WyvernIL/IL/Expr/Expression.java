package wyvern.targets.Common.wyvernIL.IL.Expr;

import wyvern.targets.Common.wyvernIL.IL.visitor.ExprVisitor;

public interface Expression {
	public <R> R accept(ExprVisitor<R> visitor);
}
