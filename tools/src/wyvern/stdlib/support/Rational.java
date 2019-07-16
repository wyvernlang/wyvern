package wyvern.stdlib.support;

import java.math.BigInteger;

public class Rational {
    private BigInteger numerator;
    private BigInteger denominator;

    public Rational(BigInteger numerator, BigInteger denominator) {
        this.numerator = numerator;
        this.denominator = denominator;
    }

    public BigInteger getDenominator() {
        return this.denominator;
    }

    public BigInteger getNumerator() {
        return this.numerator;
    }

    public String toString() {
        return numerator.toString() + "/" + denominator.toString();
    }
}
