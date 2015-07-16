package wyvern.target.oir.declarations;

import wyvern.target.oir.expressions.OIRExpression;
import wyvern.target.oir.expressions.OIRValue;

public class OIRFieldValueInitializePair {
	public OIRFieldDeclaration fieldDeclaration;
	public OIRExpression valueDeclaration;
	
	public OIRFieldValueInitializePair(OIRFieldDeclaration oirMemDecl,
			OIRExpression valueDeclaration) {
		super();
		this.fieldDeclaration = oirMemDecl;
		this.valueDeclaration = valueDeclaration;
	}
}
