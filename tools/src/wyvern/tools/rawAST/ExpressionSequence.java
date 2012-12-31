package wyvern.tools.rawAST;

import java.util.List;

public abstract class ExpressionSequence extends Sequence {
	public ExpressionSequence(List<RawAST> children) {
		super(children);
	}
	
	public RawAST getFirst() {
		return children.size() > 0 ? children.get(0) : null;
	}
	
	public abstract ExpressionSequence getRest();
}
