package wyvern.targets.Common.WyvernIL.Stmt;

import wyvern.targets.Common.WyvernIL.Expr.Expression;
import wyvern.targets.Common.WyvernIL.Imm.Operand;
import wyvern.targets.Common.WyvernIL.visitor.StatementVisitor;

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
}
