package wyvern.targets.Common.wyvernIL.IL.Def;

import java.util.LinkedList;
import java.util.List;

import wyvern.targets.Common.wyvernIL.IL.WyvIL;
import wyvern.targets.Common.wyvernIL.IL.Def.Def.Param;
import wyvern.targets.Common.wyvernIL.IL.Stmt.Statement;
import wyvern.targets.Common.wyvernIL.IL.visitor.DefVisitor;
import wyvern.tools.types.extensions.Arrow;

public class KeywordDef implements Definition{
	private String name;
	private List<Statement> body;
	
	public KeywordDef(String name, List<Statement> body) {
		this.name = name;
		this.body = body;
	}
	
	public String getName() {
		return name;
	}

	public List<Statement> getBody() {
		return body;
	}

	public Arrow getType() {
		return null; //TODO ???
	}
	
	@Override
	public <R> R accept(DefVisitor<R> visitor) {
		return visitor.visit(this);
	}
	
	@Override
	public String toString() {
		List<String> sb = new LinkedList<>();
		if (body != null)
			for (Statement def : body) {
				if (def != null)
					sb.add(def.toString());
			}

		return "keyword "+ name + " {"+ WyvIL.join(sb, ",") +"}";
	}

}
