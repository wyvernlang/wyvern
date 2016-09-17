package wyvern.target.corewyvernIL;

import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.type.NominalType;

public class Case extends ASTNode {
	private String varName;
	private NominalType pattern;
	private Expression body;
	
	public Case(String varName, NominalType pattern, Expression body) {
		super();
		this.pattern = pattern;
		this.body = body;
		this.varName = varName;
	}
	public NominalType getPattern() {
		return pattern;
	}
	public Expression getBody() {
		return body;
	}
	public String getVarName() {
		return varName;
	}
	@Override
	public <S, T> T acceptVisitor(ASTVisitor<S, T> visitor, S state) {
		// TODO Auto-generated method stub
		return visitor.visit(state, this);
	}
}
