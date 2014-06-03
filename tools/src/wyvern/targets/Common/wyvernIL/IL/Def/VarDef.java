package wyvern.targets.Common.wyvernIL.IL.Def;

import wyvern.targets.Common.wyvernIL.IL.Expr.Expression;
import wyvern.targets.Common.wyvernIL.IL.visitor.DefVisitor;
import wyvern.tools.types.Type;

public class VarDef implements Definition {

	private String name;
	private Expression exn;
	private Type type;

	public VarDef(String name, Expression exn, Type type) {
		this.name = name;
		this.exn = exn;
		this.type = type;
	}

	@Override
	public <R> R accept(DefVisitor<R> visitor) {
		return visitor.visit(this);
	}

	public Expression getExn() {
		return exn;
	}

	public String getName() {
		return name;
	}
	@Override
	public String toString() {
		return "var "+name+" = " + exn;
	}

	public Type getType() {
		return type;
	}
}