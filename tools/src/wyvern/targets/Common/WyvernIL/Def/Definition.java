package wyvern.targets.Common.WyvernIL.Def;

import wyvern.targets.Common.WyvernIL.WyvIL;
import wyvern.targets.Common.WyvernIL.visitor.DefVisitor;

public interface Definition {
	public <R> R accept(DefVisitor<R> visitor);
}
