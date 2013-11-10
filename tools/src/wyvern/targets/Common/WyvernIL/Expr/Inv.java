package wyvern.targets.Common.WyvernIL.Expr;

import wyvern.targets.Common.WyvernIL.Imm.Operand;
import wyvern.targets.Common.WyvernIL.visitor.ExprVisitor;

public class Inv implements Expression {

	private Operand source;
	private String id;

	public Inv(Operand source, String id) {
		this.source = source;
		this.id = id;
	}

	@Override
	public <R> R accept(ExprVisitor<R> visitor) {
		return visitor.visit(this);
	}

	public Operand getSource() {
		return source;
	}

	public String getId() {
		return id;
	}
}
