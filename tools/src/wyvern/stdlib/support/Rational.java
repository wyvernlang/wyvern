/*
 * program to support the interoperability between Java Rational types
 * and Wyvern RationalLiteral types.
 * @author Simon Chu
 */
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
        if (this.denominator.equals(new BigInteger("1"))) {
            // only print numerator if denominator is 1.
            return this.numerator.toString();
        } else {
            // other cases, print the division bar in between numerator and denominator.
            return numerator.toString() + "/" + denominator.toString();
        }
    }
}
