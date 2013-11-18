package wyvern.targets.Common.WyvernIL.Def;

import wyvern.targets.Common.WyvernIL.Stmt.Statement;
import wyvern.targets.Common.WyvernIL.visitor.DefVisitor;
import wyvern.tools.types.Type;

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
	}
}
