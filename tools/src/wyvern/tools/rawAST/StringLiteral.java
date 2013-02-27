package wyvern.tools.rawAST;

public class StringLiteral implements RawAST {
	public final String data;
	
	public StringLiteral(String s) {
		data = s;
	}
	
	@Override
	public <A,R> R accept(RawASTVisitor<A,R> visitor, A arg) {
		return visitor.visit(this, arg);
	}
	
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof StringLiteral))
			return false;
		StringLiteral otherData = (StringLiteral) other; 
		return otherData.data.equals(data);
	}
	
	@Override
	public int hashCode() {
		return data.hashCode();
	}
	
	@Override
	public String toString() {
		return "\"" + data + '\"';
	}
}
