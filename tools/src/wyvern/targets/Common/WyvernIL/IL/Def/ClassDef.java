package wyvern.targets.Common.wyvernIL.IL.Def;

import wyvern.targets.Common.wyvernIL.IL.WyvIL;
import wyvern.targets.Common.wyvernIL.IL.visitor.DefVisitor;
import wyvern.tools.types.Type;

import java.util.LinkedList;
import java.util.List;

public class ClassDef implements Definition {
	private String name;
	private List<Definition> definitions;
	private List<Definition> classDefinitions;
	private Type type;

	public ClassDef(String name, List<Definition> definitions, List<Definition> classDefinitions, Type type) {
		this.name = name;
		this.definitions = definitions;
		this.classDefinitions = classDefinitions;
		this.type = type;
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

	public List<Definition> getClassDefinitions() {
		return classDefinitions;
	}


	@Override
	public String toString() {
		List<String> sb = new LinkedList<>();
		for (Definition def : definitions) {
			sb.add(def.toString());
		}
		List<String> ssb = new LinkedList<>();
		for (Definition def : classDefinitions) {
			ssb.add(def.toString());
		}

		return "class "+name+" { static {" + WyvIL.join(ssb, "; ") + "}; "+ WyvIL.join(sb, "; ") +"}";
	}

	public Type getType() {
		return type;
	}
}
