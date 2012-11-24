package wyvern.tools.rawAST;

public class StringNode implements RawAST {
	public final String data;
	
	public StringNode(String s) {
		data = s;
	}
	
	@Override
	public <A,R> R accept(RawASTVisitor<A,R> visitor, A arg) {
		return visitor.visit(this, arg);
	}
	
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof StringNode))
			return false;
		StringNode otherData = (StringNode) other; 
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
