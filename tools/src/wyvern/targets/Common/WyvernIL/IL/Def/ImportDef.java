package wyvern.targets.Common.wyvernIL.IL.Def;

import wyvern.targets.Common.wyvernIL.IL.visitor.DefVisitor;

import java.net.URI;

public class ImportDef implements Definition {
	private URI source;

	public ImportDef(URI source) {
		this.source = source;
	}

	public URI getSource() {
		return source;
	}

	@Override
	public <R> R accept(DefVisitor<R> visitor) {
		return visitor.visit(this);
	}
}
