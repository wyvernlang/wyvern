package wyvern.tools.rawAST;

import wyvern.tools.typedAST.core.values.IntegerConstant;

/**
 * Created by Ben Chung on 12/2/13.
 */
public class Codifier implements RawASTVisitor<Integer,String> {

	private String tabify(int level) {
		StringBuilder res = new StringBuilder(level);
		for (int i = 0; i < level; i++)
			res.append("\t");
		return res.toString();
	}

	private String sanitize(String inp) {
		String reped = inp.replaceAll("\n+","\n");
		return reped;
	}
	@Override
	public String visit(IntLiteral node, Integer l) {
		return node.data + "";
	}

	@Override
	public String visit(StringLiteral node, Integer l) {
		return " \"" + node.data + "\"";
	}

	@Override
	public String visit(Symbol node, Integer l) {
		return node.name;
	}

	@Override
	public String visit(Unit node, Integer l) {
		return "()";
	}

	@Override
	public String visit(LineSequence node, Integer l) {
		StringBuilder sb = new StringBuilder("\n");
		for (RawAST child : node.children) {
			sb.append(tabify(l) + child.accept(this, l) + "\n");
		}
		return sanitize(sb.toString());
	}

	@Override
	public String visit(Line node, Integer l) {
		StringBuilder sb = new StringBuilder();
		String sep = "";
		for (RawAST child : node.children) {
			sb.append(sep + child.accept(this, l+1));
			sep = " ";
		}
		return sanitize(sb.toString());
	}

	@Override
	public String visit(Parenthesis node, Integer l) {
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		String delim = "";
		for (RawAST child : node.children) {
			sb.append(delim).append(child.accept(this, l));
			delim = ",";
		}
		sb.append(")");
		return sanitize(sb.toString());
	}
}
