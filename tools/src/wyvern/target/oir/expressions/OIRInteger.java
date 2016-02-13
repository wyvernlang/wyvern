package wyvern.target.oir.expressions;

import wyvern.target.oir.ASTVisitor;
import wyvern.target.oir.OIREnvironment;
import wyvern.target.oir.declarations.OIRIntegerType;
import wyvern.target.oir.declarations.OIRType;

public class OIRInteger extends OIRLiteral implements OIRValue {
	private int value;

	public OIRInteger(int value) {
		super();
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	@Override
	public OIRType typeCheck(OIREnvironment oirEnv) {
		setExprType (OIRIntegerType.getIntegerType());
		return getExprType ();
	}

	@Override
	public <T> T acceptVisitor(ASTVisitor<T> visitor, OIREnvironment oirenv) {
		return visitor.visit(oirenv, this);
	}
}
