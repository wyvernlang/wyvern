package wyvern.targets.Common.WyvernIL.Def;

import wyvern.targets.Common.WyvernIL.WyvIL;
import wyvern.targets.Common.WyvernIL.visitor.DefVisitor;

import java.util.LinkedList;
import java.util.List;

public class TypeDef implements Definition {
	private String name;
	private List<Definition> definitions;

	public TypeDef(String name, List<Definition> definitions) {
		this.name = name;
		this.definitions = definitions;
	}

	@Override
	public <R> R accept(DefVisitor<R> visitor) {
		return visitor.visit(this);
	}

	public List<Definition> getDefinitions() {
		return definitions;
	}

	public String getName() {
		return name;
	}
	@Override
	public String toString() {
		List<String> sb = new LinkedList<>();
		for (Definition def : definitions) {
			sb.add(def.toString());
		}

		return "class "+name+" {"+ WyvIL.join(sb, "; ") +"}";
	}
}
