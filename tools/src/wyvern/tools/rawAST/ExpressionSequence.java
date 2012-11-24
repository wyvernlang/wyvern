package wyvern.tools.rawAST;

import java.util.List;

public abstract class ExpressionSequence extends Sequence {
	public ExpressionSequence(List<RawAST> children) {
		super(children);
	}
	
	public RawAST getFirst() {
		return children.get(0);
	}
	
	public abstract ExpressionSequence getRest();
}
