package wyvern.targets.Common.WyvernIL.Expr;

import wyvern.targets.Common.WyvernIL.visitor.ExprVisitor;

/**
 * Created by Ben Chung on 11/11/13.
 */
public class New implements Expression {
	private String src;

	public New(String src) {
		this.src = src;
	}

	@Override
	public <R> R accept(ExprVisitor<R> visitor) {
		return visitor.visit(this);
	}

	public String getSrc() {
		return src;
	}
}
