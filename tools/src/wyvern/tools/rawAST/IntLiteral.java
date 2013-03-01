package wyvern.tools.rawAST;


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

	private int line = -1;
	public int getLine() {
		return this.line; // TODO: NOT IMPLEMENTED YET.
	}
}
