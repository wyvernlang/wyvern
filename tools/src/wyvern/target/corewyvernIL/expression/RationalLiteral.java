package wyvern.target.corewyvernIL.expression;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.effects.EffectAccumulator;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.support.Util;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.FileLocation;

public class RationalLiteral extends Literal implements Invokable {

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

        // throws RuntimeException if the denominator is zero.
        if (denominator.equals(BigInteger.ZERO)) {
            throw new RuntimeException("Denominator cannot be zero!");
        }
        BigInteger greatCommonDivisor = gcd(numerator, denominator);
        this.numerator = numerator.divide(greatCommonDivisor);
        this.denominator = denominator.divide(greatCommonDivisor);
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

    @Override
    public Value invoke(String methodName, List<Value> args, FileLocation loc) {
        switch (methodName) {
            case "+": return this.add((RationalLiteral) args.get(0));
            case "-": return this.subtract((RationalLiteral) args.get(0));
            case "*": return this.multiply((RationalLiteral) args.get(0));
            case "/": return this.divide((RationalLiteral) args.get(0));
            case "negate": return this.negate();
            case "<": return new BooleanLiteral(this.compareTo(((RationalLiteral) args.get(0))) < 0);
            case ">": return new BooleanLiteral(this.compareTo(((RationalLiteral) args.get(0))) > 0);
            case "==": return new BooleanLiteral(this.compareTo(((RationalLiteral) args.get(0))) == 0);
            case "<=": return new BooleanLiteral(this.compareTo(((RationalLiteral) args.get(0))) <= 0);
            case ">=": return new BooleanLiteral(this.compareTo(((RationalLiteral) args.get(0))) >= 0);
            case "!=": return new BooleanLiteral(this.compareTo(((RationalLiteral) args.get(0))) != 0);

            default: throw new RuntimeException("runtime error: integer operation " + methodName + "not supported by the runtime");
        }
    }

    @Override
    public Value getField(String fieldName) {
        throw new RuntimeException("no fields");
    }

    /*
     * helper function to compute the greatest common divisor.
     * @param numerator the numerator of the rational number.
     * @param denominator the denominator of the rational number.
     * @returns the greatest common divisor of the numerator and denominator.
     */
    private static BigInteger gcd(BigInteger numerator, BigInteger denominator) {
        if (denominator.equals(BigInteger.ZERO)) {
            return numerator;
        } else {
            return gcd(denominator, numerator.mod(denominator));
        }
    }

    /*
     * perform addition operation.
     * @param rhs the rational number on the right hand side.
     * @returns result containing operation result.
     */
    public RationalLiteral add(RationalLiteral rhs) {
        BigInteger numerator = this.numerator.multiply(rhs.denominator).add(this.denominator.multiply(rhs.numerator));
        BigInteger denominator = this.denominator.multiply(rhs.denominator);
        return new RationalLiteral(numerator, denominator);
    }

    /*
     * perform subtract operation.
     * @param rhs the rational number on the right hand side.
     * @returns result containing operation result.
     */
    public RationalLiteral subtract(RationalLiteral rhs) {
        return this.add(rhs.negate());
    }

    /*
     * perform multiply operation.
     * @param rhs the rational number on the right hand side.
     * @returns result containing operation result.
     */
    public RationalLiteral multiply(RationalLiteral rhs) {
        return new RationalLiteral(this.numerator.multiply(rhs.numerator), this.denominator.multiply(rhs.denominator));
    }

    /*
     * perform divide operation.
     * @param rhs the rational number on the right hand side.
     * @returns result containing operation result.
     */
    public RationalLiteral divide(RationalLiteral rhs) {
        return this.multiply(rhs.reciprocal());
    }

    /*
     * perform comparison operation between the rational numbers.
     * @param rhs the rational number on the right hand side.
     * @returns result containing operation result.
     */
    public int compareTo(RationalLiteral rhs) {
        RationalLiteral difference = this.subtract(rhs);
        if (difference.numerator.compareTo(BigInteger.ZERO) > 0) {
            return 1;
        }
        if (difference.numerator.compareTo(BigInteger.ZERO) < 0) {
            return -1;
        }
        return 0;
    }

    /*
     * compute the negation of the current rational number
     * @returns a new BigInteger object with negated value of the current rational number object.
     */
    public RationalLiteral negate() {
        return new RationalLiteral(this.numerator.negate(), this.denominator, this.getLocation());
    }

    /*
     * compute the reciprocal of the current rational number
     * @returns a new BigInteger object with negated value of the current rational number object.
     */
    public RationalLiteral reciprocal() {
        return new RationalLiteral(this.denominator, this.numerator, this.getLocation());
    }
}
