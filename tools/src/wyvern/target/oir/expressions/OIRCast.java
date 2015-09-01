package wyvern.target.oir.expressions;

import wyvern.target.oir.ASTVisitor;
import wyvern.target.oir.OIRAST;
import wyvern.target.oir.OIREnvironment;
import wyvern.target.oir.declarations.OIRType;

public class OIRCast extends OIRExpression {
	private OIRExpression toCastEXpr;

	public OIRCast(OIRExpression toCastEXpr, OIRType type) {
		super();
		this.toCastEXpr = toCastEXpr;
		this.setExprType(type);
	}

	public OIRExpression getToCastEXpr() {
		return toCastEXpr;
	}

	public void setToCastEXpr(OIRExpression toCastEXpr) {
		this.toCastEXpr = toCastEXpr;
	}

	@Override
	public OIRType typeCheck(OIREnvironment oirEnv) {
		/* TODO: Check if the Type Exists in the OIREnvironment or not */
		return getExprType ();
	}

	@Override
	public <T> T acceptVisitor(ASTVisitor<T> visitor, OIREnvironment oirenv) {
		return visitor.visit(oirenv, this);
	}
}
