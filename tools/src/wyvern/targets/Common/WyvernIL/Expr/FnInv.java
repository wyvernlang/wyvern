package wyvern.targets.Common.WyvernIL.Expr;

import wyvern.targets.Common.WyvernIL.Imm.Operand;
import wyvern.targets.Common.WyvernIL.visitor.ExprVisitor;

import java.util.List;

public class FnInv implements Expression {

	private Operand fn;
	private List<Operand> args;

	public FnInv(Operand fn, List<Operand> args) {
		this.fn = fn;
		this.args = args;
	}

	@Override
	public <R> R accept(ExprVisitor<R> visitor) {
		return visitor.visit(this);
	}

	public List<Operand> getArgs() {
		return args;
	}

	public Operand getFn() {
		return fn;
	}
}
