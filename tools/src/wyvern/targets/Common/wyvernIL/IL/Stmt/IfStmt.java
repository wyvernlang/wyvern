package wyvern.targets.Common.wyvernIL.IL.Stmt;

import wyvern.targets.Common.wyvernIL.IL.Expr.Expression;
import wyvern.targets.Common.wyvernIL.IL.visitor.StatementVisitor;

public class IfStmt implements Statement {

	private Expression condition;
	private Label label;

	public IfStmt(Expression condition, Label label) {
		this.condition = condition;
		this.label = label;
	}

	@Override
	public <R> R accept(StatementVisitor<R> visitor) {
		return visitor.visit(this);
	}

	public Label getLabel() {
		return label;
	}

	public Expression getCondition() {
		return condition;
	}


	@Override
	public String toString() {
		return "if (" + condition + ") goto " + label;
	}
}
