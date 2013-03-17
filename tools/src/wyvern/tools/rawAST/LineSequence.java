package wyvern.tools.rawAST;

import java.util.List;

public class LineSequence extends Sequence {
	public LineSequence(List<RawAST> children, int line) {
		super(children);
		this.line = line;
	}

	public LineSequence(int line) {
		this.line = line;
	}

	@Override
	public <A,R> R accept(RawASTVisitor<A,R> visitor, A arg) {
		return visitor.visit(this, arg);
	}
	
	@Override
	protected String getOpenChar() {
		return "{$I ";
	}
	@Override
	protected String getCloseChar() {
		return " $I}";
	}

	public Line getFirst() {
		return (Line) children.get(0);
	}
	
	public LineSequence getRest() {
		if (children.size() == 1)
			return null;
		else
			return new LineSequence(children.subList(1, children.size()), this.line);
	}

	private int line;
	public int getLine() {
		return this.line;
	}
}
