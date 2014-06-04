package wyvern.targets.Common.wyvernIL.IL.Def;

import wyvern.targets.Common.wyvernIL.IL.Stmt.Statement;
import wyvern.targets.Common.wyvernIL.IL.WyvIL;
import wyvern.targets.Common.wyvernIL.IL.visitor.DefVisitor;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.Arrow;

import java.util.LinkedList;
import java.util.List;

public class Def implements Definition {
	private String name;
	private List<Param> params;
	private List<Statement> body;

	public Def(String name, List<Param> params, List<Statement> body) {
		this.name = name;
		this.params = params;
		this.body = body;
	}

	@Override
	public <R> R accept(DefVisitor<R> visitor) {
		return visitor.visit(this);
	}

	public String getName() {
		return name;
	}

	public List<Param> getParams() {
		return params;
	}

	public List<Statement> getBody() {
		return body;
	}

	public Arrow getType() {
		return null; //TODO
	}

	public static class Param {
		private String name;
		private Type type;

		public Param(String name, Type type) {
			this.name = name;
			this.type = type;
		}

		public Type getType() {
			return type;
		}

		public String getName() {
			return name;
		}

		@Override
		public String toString() {
			return name+" : "+type;
		}
	}

	@Override
	public String toString() {
		List<String> sb = new LinkedList<>();
		if (body != null)
			for (Statement def : body) {
				if (def != null)
					sb.add(def.toString());
			}
		List<String> argsb = new LinkedList<>();
		for (Param def : params) {
			argsb.add(def.toString());
		}

		return "def "+name+"(" + WyvIL.join(argsb,",")+ ") {"+ WyvIL.join(sb, ",") +"}";
	}
}
