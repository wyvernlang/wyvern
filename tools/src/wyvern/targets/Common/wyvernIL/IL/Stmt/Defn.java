package wyvern.targets.Common.wyvernIL.IL.Stmt;

import wyvern.targets.Common.wyvernIL.IL.Def.Definition;
import wyvern.targets.Common.wyvernIL.IL.visitor.StatementVisitor;

public class Defn implements Statement {

	private Definition definition;

	public Defn(Definition definition) {
		this.definition = definition;
	}

	@Override
	public <R> R accept(StatementVisitor<R> visitor) {
		return visitor.visit(this);
	}

	public Definition getDefinition() {
		return definition;
	}

	@Override
	public String toString() {
		return definition.toString();
	}
}
