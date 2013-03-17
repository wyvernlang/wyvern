package wyvern.tools.rawAST;

import java.util.List;

import wyvern.tools.errors.FileLocation;

public class Line extends ExpressionSequence {
	public Line(List<RawAST> children, FileLocation location) {
		super(children);
		this.location = location;
	}
	
	@Override
	public <A,R> R accept(RawASTVisitor<A,R> visitor, A arg) {
		return visitor.visit(this, arg);
	}
	
	@Override
	protected String getOpenChar() {
		return "{$L ";
	}
	
	@Override
	protected String getCloseChar() {
		return " $L}";
	}
	
	@Override
	public Line getRest() {
		if (children.size() == 1)
			return null;
		else
			return new Line(children.subList(1, children.size()), this.location);
	}

	private FileLocation location = FileLocation.UNKNOWN;
	
	@Override
	public FileLocation getLocation() {
		return location; // TODO: NOT IMPLEMENTED YET.
	}
}
