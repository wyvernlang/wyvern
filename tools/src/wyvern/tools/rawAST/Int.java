package wyvern.tools.rawAST;


public class Int implements RawAST {
	public final int data;
	
	public Int(int i) {
		data = i;
	}
	
	@Override
	public <A,R> R accept(RawASTVisitor<A,R> visitor, A arg) {
		return visitor.visit(this, arg);
	}
	
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof Int))
			return false;
		Int otherData = (Int) other; 
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
}
