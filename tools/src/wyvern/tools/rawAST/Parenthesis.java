package wyvern.tools.rawAST;

import java.util.List;

public class Parenthesis extends ExpressionSequence {
	public Parenthesis(List<RawAST> children) {
		super(children);
	}
	
	@Override
	public <A,R> R accept(RawASTVisitor<A,R> visitor, A arg) {
		return visitor.visit(this, arg);
	}
	
	@Override
	protected String getOpenChar() {
		return "(";
	}
	
	@Override
	protected String getCloseChar() {
		return ")";
	}
	
	@Override
	public Parenthesis getRest() {
		if (children.size() == 1)
			return null;
		else
			return new Parenthesis(children.subList(1, children.size()));
	}

	public int getLine() {
		return -1; // TODO: NOT IMPLEMENTED YET.
	}
}
