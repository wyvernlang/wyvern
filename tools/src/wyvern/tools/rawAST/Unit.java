package wyvern.tools.rawAST;

import wyvern.tools.errors.FileLocation;


public class Unit implements RawAST {
	private Unit() { }
	private static Unit instance = new Unit();
	public static Unit getInstance() { return instance; }
	
	@Override
	public <A,R> R accept(RawASTVisitor<A,R> visitor, A arg) {
		return visitor.visit(this, arg);
	}
	
	@Override
	public String toString() {
		return "()";
	}

	private FileLocation location = FileLocation.UNKNOWN;
	
	@Override
	public FileLocation getLocation() {
		return location; // TODO: NOT IMPLEMENTED YET.
	}
}
