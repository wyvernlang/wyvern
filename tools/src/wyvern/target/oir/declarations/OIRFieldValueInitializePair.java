package wyvern.target.oir.declarations;

import wyvern.target.oir.expressions.OIRExpression;

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
