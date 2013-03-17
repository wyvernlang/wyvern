package wyvern.tools.rawAST;

import wyvern.tools.errors.FileLocation;


public class IntLiteral implements RawAST {
	public final int data;
	
	public IntLiteral(int i) {
		data = i;
	}
	
	@Override
	public <A,R> R accept(RawASTVisitor<A,R> visitor, A arg) {
		return visitor.visit(this, arg);
	}
	
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof IntLiteral))
			return false;
		IntLiteral otherData = (IntLiteral) other; 
		return otherData.data == data;
	}
	
	@Override
	public int hashCode() {
		return data;
	}
	
	@Override
	public String toString() {
		return Integer.toString(data);
	}

	private FileLocation location = FileLocation.UNKNOWN;
	
	@Override
	public FileLocation getLocation() {
		return location; // TODO: NOT IMPLEMENTED YET.
	}
}
