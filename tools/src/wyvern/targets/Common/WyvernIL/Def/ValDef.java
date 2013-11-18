package wyvern.targets.Common.WyvernIL.Def;

import wyvern.targets.Common.WyvernIL.Expr.Expression;
import wyvern.targets.Common.WyvernIL.Imm.Operand;
import wyvern.targets.Common.WyvernIL.Stmt.Pure;
import wyvern.targets.Common.WyvernIL.WyvIL;
import wyvern.targets.Common.WyvernIL.visitor.DefVisitor;

import java.util.LinkedList;
import java.util.List;

public class ValDef implements Definition {
	private String name;
	private Expression exn;

	public ValDef(String name, Expression exn) {
		this.name = name;
		this.exn = exn;
	}

	@Override
	public <R> R accept(DefVisitor<R> visitor) {
		return visitor.visit(this);
	}

	public String getName() {
		return name;
	}

	public Expression getExn() {
		return exn;
	}
	@Override
	public String toString() {
		return "val "+name+" = " + exn;
	}
}