package wyvern.target.oir.declarations;

import wyvern.target.oir.expressions.OIRValue;

public class OIRFieldValueInitializePair {
	public OIRFieldDeclaration fieldDeclaration;
	public OIRValue valueDeclaration;
	
	public OIRFieldValueInitializePair(OIRFieldDeclaration oirMemDecl,
			OIRValue valueDeclaration) {
		super();
		this.fieldDeclaration = oirMemDecl;
		this.valueDeclaration = valueDeclaration;
	}
}
