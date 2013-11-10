package wyvern.targets.Common.WyvernIL.Stmt;

import wyvern.targets.Common.WyvernIL.Expr.Expression;
import wyvern.targets.Common.WyvernIL.visitor.StatementVisitor;

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
}
