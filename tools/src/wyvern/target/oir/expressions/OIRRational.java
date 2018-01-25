package wyvern.target.oir.expressions;

import wyvern.target.oir.ASTVisitor;
import wyvern.target.oir.OIREnvironment;
import wyvern.target.oir.declarations.OIRRationalType;
import wyvern.target.oir.declarations.OIRType;

public class OIRRational extends OIRLiteral implements OIRValue {
    private int numerator;
    private int denominator;

    public int getNumerator() {
        return numerator;
    }

    public void setNumerator(int numerator) {
        this.numerator = numerator;
    }

    public int getDenominator() {
        return denominator;
    }

    public void setDenominator(int denominator) {
        this.denominator = denominator;
    }

    public OIRRational(int numerator, int denominator) {
        super();
        this.numerator = numerator;
        this.denominator = denominator;
    }

    @Override
    public OIRType typeCheck(OIREnvironment oirEnv) {
        setExprType(OIRRationalType.getRationalType());
        return getExprType();
    }

    @Override
    public <S, T> T acceptVisitor(ASTVisitor<S, T> visitor, S state) {
        return visitor.visit(state, this);
    }
}
