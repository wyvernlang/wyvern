package wyvern.target.oir.expressions;

import wyvern.target.oir.ASTVisitor;
import wyvern.target.oir.OIREnvironment;
import wyvern.target.oir.declarations.OIRRationalType;
import wyvern.target.oir.declarations.OIRType;

import java.math.BigInteger;

public class OIRRational extends OIRLiteral implements OIRValue {
    private BigInteger numerator;
    private BigInteger denominator;

    public BigInteger getNumerator() {
        return numerator;
    }

    public void setNumerator(BigInteger numerator) {
        this.numerator = numerator;
    }

    public BigInteger getDenominator() {
        return denominator;
    }

    public void setDenominator(BigInteger denominator) {
        this.denominator = denominator;
    }

    public OIRRational(BigInteger numerator, BigInteger denominator) {
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
