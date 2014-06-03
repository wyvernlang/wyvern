package wyvern.targets.Common.wyvernIL.IL.Expr;

import wyvern.targets.Common.wyvernIL.IL.Imm.Operand;
import wyvern.targets.Common.wyvernIL.IL.visitor.ExprVisitor;

public class FnInv implements Expression {

	private Operand fn;
	private Operand arg;

	public FnInv(Operand fn, Operand args) {
		this.fn = fn;
		this.arg = args;
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
		if (arg != null)
			return fn.toString() + "(" + arg + ")";
		else
			return fn.toString() + "()";

	}
}
