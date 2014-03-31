package wyvern.targets.Common.wyvernIL.IL.Def;

import wyvern.targets.Common.wyvernIL.IL.Expr.Expression;
import wyvern.targets.Common.wyvernIL.IL.visitor.DefVisitor;

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