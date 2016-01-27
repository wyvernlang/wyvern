package wyvern.target.oir.expressions;

import wyvern.target.oir.ASTVisitor;
import wyvern.target.oir.OIREnvironment;
import wyvern.target.oir.declarations.OIRBooleanType;
import wyvern.target.oir.declarations.OIRType;

public class OIRBoolean extends OIRLiteral {
	private boolean value;

	public OIRBoolean(boolean value) {
		super();
		this.value = value;
	}

	public boolean isValue() {
		return value;
	}

	public void setValue(boolean value) {
		this.value = value;
	}

	@Override
	public OIRType typeCheck(OIREnvironment oirEnv) {
		setExprType(OIRBooleanType.getBooleanType());
		return getExprType ();
	}

	@Override
	public <T> T acceptVisitor(ASTVisitor<T> visitor, OIREnvironment oirenv) {
		return visitor.visit(oirenv, this);
	}
}
