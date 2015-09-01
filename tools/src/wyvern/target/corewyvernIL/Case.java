package wyvern.target.corewyvernIL;

import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.type.NominalType;
import wyvern.target.corewyvernIL.type.Type;

public class Case extends ASTNode {
	private String varName;
	private NominalType pattern;
	private Expression body;
	
	public Case(String varName, NominalType pattern, Expression body) {
		super();
		this.pattern = pattern;
		this.body = body;
		this.setVarName(varName);
	}
	public NominalType getPattern() {
		return pattern;
	}
	public void setPattern(NominalType pattern) {
		this.pattern = pattern;
	}
	public Expression getBody() {
		return body;
	}
	public void setBody(Expression body) {
		this.body = body;
	}
	public String getVarName() {
		return varName;
	}
	public void setVarName(String varName) {
		this.varName = varName;
	}
}
