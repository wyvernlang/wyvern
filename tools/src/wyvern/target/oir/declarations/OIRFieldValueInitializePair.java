package wyvern.target.oir.declarations;

import wyvern.target.oir.expressions.OIRExpression;

public class OIRFieldValueInitializePair {
    private final OIRFieldDeclaration fieldDeclaration;
    private final OIRExpression valueDeclaration;

    public OIRFieldValueInitializePair(OIRFieldDeclaration oirMemDecl,
            OIRExpression valueDeclaration) {
        super();
        this.fieldDeclaration = oirMemDecl;
        this.valueDeclaration = valueDeclaration;
    }

    public OIRFieldDeclaration getFieldDeclaration() {
        return fieldDeclaration;
    }

    public OIRExpression getValueDeclaration() {
        return valueDeclaration;
    }
}
