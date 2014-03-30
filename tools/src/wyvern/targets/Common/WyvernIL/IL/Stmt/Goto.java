package wyvern.targets.Common.wyvernIL.IL.Stmt;

import wyvern.targets.Common.wyvernIL.IL.visitor.StatementVisitor;

public class Goto implements Statement {

	private Label label;

	public Goto(Label label) {
		this.label = label;
	}

	@Override
	public <R> R accept(StatementVisitor<R> visitor) {
		return visitor.visit(this);
	}

	public Label getLabel() {
		return label;
	}



	@Override
	public String toString() {
		return "goto " + label;
	}
}
