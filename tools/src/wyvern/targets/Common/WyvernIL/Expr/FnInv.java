package wyvern.targets.Common.WyvernIL.Expr;

import wyvern.targets.Common.WyvernIL.Imm.Operand;
import wyvern.targets.Common.WyvernIL.visitor.ExprVisitor;

import java.util.List;

public class FnInv implements Expression {

	private Operand fn;
	private Operand arg;

	public FnInv(Operand fn, Operand args) {
		this.fn = fn;
		this.arg = arg;
	}

	@Override
	public <R> R accept(ExprVisitor<R> visitor) {
		return visitor.visit(this);
	}

	public Operand getArg() {
		return arg;
	}

	public Operand getFn() {
		return fn;
	}


	@Override
	public String toString() {
		return fn.toString() + "(" + arg.toString() + ")";
	}
}
