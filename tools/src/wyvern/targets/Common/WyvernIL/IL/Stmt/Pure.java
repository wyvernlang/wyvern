package wyvern.targets.Common.wyvernIL.IL.Stmt;

import wyvern.targets.Common.wyvernIL.IL.Expr.Expression;
import wyvern.targets.Common.wyvernIL.IL.visitor.StatementVisitor;

public class Pure implements Statement {

	private Expression expression;

	public Pure(Expression expression) {
		this.expression = expression;
	}

	@Override
	public <R> R accept(StatementVisitor<R> visitor) {
		return visitor.visit(this);
	}

	public Expression getExpression() {
		return expression;
	}

	@Override
	public String toString() {
		return (expression != null)?expression.toString():"";
	}
}
