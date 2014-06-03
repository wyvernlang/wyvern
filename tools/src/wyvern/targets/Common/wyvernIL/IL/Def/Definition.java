package wyvern.targets.Common.wyvernIL.IL.Def;

import wyvern.targets.Common.wyvernIL.IL.visitor.DefVisitor;

public interface Definition {
	public <R> R accept(DefVisitor<R> visitor);
}
