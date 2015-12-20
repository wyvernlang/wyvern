package wyvern.target.corewyvernIL;

import java.io.IOException;

public abstract class ASTNode {
	public final String prettyPrint() throws IOException {
		Appendable dest = new StringBuilder(); 
		doPrettyPrint(dest, "");
		return dest.toString();
	}
	public String toString() {
		try {
			return prettyPrint();
		} catch (IOException e) {
			e.printStackTrace();
			return "ERROR_PRINTING";
		}
	}
	public void doPrettyPrint(Appendable dest, String indent) throws IOException {
		dest.append("NOT_IMPLEMENTED(")
			.append(this.getClass().getName())
			.append(')');
		//throw new RuntimeException("not implemented");
	}
}
