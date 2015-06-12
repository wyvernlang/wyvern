package wyvern.target.corewyvernIL;

import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.type.Type;

public class Case extends ASTNode {

	private Type pattern;
	private Expression body;
	
	public Case(Type pattern, Expression body) {
		super();
		this.pattern = pattern;
		this.body = body;
	}
	public Type getPattern() {
		return pattern;
	}
	public void setPattern(Type pattern) {
		this.pattern = pattern;
	}
	public Expression getBody() {
		return body;
	}
	public void setBody(Expression body) {
		this.body = body;
	}
}
