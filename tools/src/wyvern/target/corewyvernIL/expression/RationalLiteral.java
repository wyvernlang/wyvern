package wyvern.target.corewyvernIL.expression;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;

import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.effects.EffectAccumulator;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.support.Util;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.FileLocation;

public class RationalLiteral extends Literal {

    private BigInteger numerator;
    private BigInteger denominator;

    public RationalLiteral(int numerator, int denominator) {
        this(BigInteger.valueOf(numerator), BigInteger.valueOf(denominator), FileLocation.UNKNOWN);
    }

    public RationalLiteral(BigInteger numerator, BigInteger denominator) {
        this(numerator, denominator, FileLocation.UNKNOWN);
    }

    public RationalLiteral(int numerator, int denominator, FileLocation loc) {
        this(BigInteger.valueOf(numerator), BigInteger.valueOf(denominator), loc);
    }

    public RationalLiteral(BigInteger numerator, BigInteger denominator, FileLocation loc) {
        super(null, loc);
        this.numerator = numerator;
        this.denominator = denominator;
    }

    public BigInteger getNumerator() {
        return numerator;
    }

    public BigInteger getDenominator() {
        return denominator;
    }

    public void setNumerator(int numerator) {
        this.numerator = BigInteger.valueOf(numerator);
    }

    public void setNumerator(BigInteger numerator) {
        this.numerator = numerator;
    }

    public void setDenominator(int denominator) {
        this.denominator = BigInteger.valueOf(denominator);
    }

    public void setDenominator(BigInteger denominator) {
        this.denominator = denominator;
    }

    @Override
    public ValueType typeCheck(TypeContext env, EffectAccumulator effectAccumulator) {
        return Util.rationalType();
    }

    @Override
    public <S, T> T acceptVisitor(ASTVisitor<S, T> emitILVisitor, S state) {
        return emitILVisitor.visit(state, this);
    }

    public Set<String> getFreeVariables() {
        return new HashSet<>();
    }

    @Override
    public ValueType getType() {
        return Util.rationalType();
    }
}
