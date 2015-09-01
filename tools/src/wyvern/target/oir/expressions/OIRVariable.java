package wyvern.target.oir.expressions;

import wyvern.target.oir.ASTVisitor;
import wyvern.target.oir.OIREnvironment;
import wyvern.target.oir.declarations.OIRType;

public class OIRVariable extends OIRExpression{
	private String name;

	public OIRVariable(String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public OIRType typeCheck(OIREnvironment oirEnv) {
		setExprType (oirEnv.lookup(name));
		return getExprType ();
	}

	@Override
	public <T> T acceptVisitor(ASTVisitor<T> visitor, OIREnvironment oirenv) {
		return visitor.visit(oirenv, this);
	}
}
