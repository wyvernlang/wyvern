package wyvern.tools.rawAST;

public class Symbol implements RawAST {
	public final String name;
	
	public Symbol(String s, int line) {
		name = s.intern();
		this.line = line;
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

	private int line;
	public int getLine() {
		return this.line;
	}
}
