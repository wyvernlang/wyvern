package wyvern.tools.rawAST;

import wyvern.tools.errors.FileLocation;

public class Symbol implements RawAST {
	public final String name;
	
	public Symbol(String s, FileLocation location) {
		name = s.intern();
		this.location = location;
	}
	
	@Override
	public <A,R> R accept(RawASTVisitor<A,R> visitor, A arg) {
		return visitor.visit(this, arg);
	}
	
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof Symbol))
			return false;
		Symbol otherData = (Symbol) other; 
		return otherData.name==name;
	}
	
	@Override
	public int hashCode() {
		return name.hashCode();
	}
	
	@Override
	public String toString() {
		return name;
	}

	private FileLocation location = FileLocation.UNKNOWN;
	
	@Override
	public FileLocation getLocation() {
		return location; // TODO: NOT IMPLEMENTED YET.
	}
}
