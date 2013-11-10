package wyvern.targets.Common.WyvernIL.Def;

import wyvern.targets.Common.WyvernIL.visitor.DefVisitor;

import java.util.List;

public class ClassDef implements Definition {
	private String name;
	private List<Definition> definitions;

	public ClassDef(String name, List<Definition> definitions) {
		this.name = name;
		this.definitions = definitions;
	}

	@Override
	public <R> R accept(DefVisitor<R> visitor) {
		return visitor.visit(this);
	}

	public String getName() {
		return name;
	}

	public List<Definition> getDefinitions() {
		return definitions;
	}
}
