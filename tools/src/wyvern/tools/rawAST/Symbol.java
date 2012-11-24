package wyvern.tools.rawAST;

public class Symbol implements RawAST {
	public final String name;
	
	public Symbol(String s) {
		name = s.intern();
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
}
