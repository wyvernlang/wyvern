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

	public class Param {
		public String name;
		public Type type;
	}
}
