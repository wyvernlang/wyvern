package wyvern.target.oir.declarations;

import wyvern.target.oir.ASTVisitor;
import wyvern.target.oir.OIRAST;
import wyvern.target.oir.OIREnvironment;

public abstract class OIRType extends OIRAST {
	public abstract String getName ();
}
