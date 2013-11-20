package wyvern.targets.Common.WyvernIL.Expr;

import wyvern.targets.Common.WyvernIL.Imm.Operand;
import wyvern.targets.Common.WyvernIL.visitor.ExprVisitor;

public class BinOp implements Expression {

	private Operand l, r;
	private String op;

	public BinOp(Operand l, Operand r, String op) {
		this.l = l;
		this.r = r;
		this.op = op;
	}

	@Override
	public <R> R accept(ExprVisitor<R> visitor) {
		return visitor.visit(this);
	}

	public String getOp() {
		return op;
	}

	public Operand getL() {
		return l;
	}

	public Operand getR() {
		return r;
	}
	@Override
	public String toString() {
		return l +" " + op + " " + r;
	}
}
