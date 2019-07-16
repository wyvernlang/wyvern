package wyvern.tools.typedAST.core.values;

import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.expression.RationalLiteral;
import wyvern.target.corewyvernIL.modules.TypedModuleSpec;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.typedAST.abs.AbstractExpressionAST;
import wyvern.tools.typedAST.interfaces.CoreAST;
import wyvern.tools.typedAST.interfaces.InvokableValue;
import wyvern.tools.typedAST.typedastvisitor.TypedASTVisitor;

import java.math.BigInteger;
import java.util.List;

public class RationalConstant extends AbstractExpressionAST implements InvokableValue, CoreAST {
    private BigInteger numerator;
    private BigInteger denominator;
    private FileLocation location = FileLocation.UNKNOWN;

    public RationalConstant(BigInteger numerator, BigInteger denominator, FileLocation loc) {
        this.numerator = numerator;
        this.denominator = denominator;
        this.location = loc;
    }

    @Override
    public Expression generateIL(GenContext ctx, ValueType expectedType, List<TypedModuleSpec> dependencies) {
        return new RationalLiteral(numerator, denominator, location);
    }

    public StringBuilder prettyPrint() {
        StringBuilder sb = new StringBuilder();
        sb.append("RationalConstant(");
        sb.append(numerator);
        sb.append("/");
        sb.append(denominator);
        sb.append(")");
        return sb;
    }

    @Override
    public <S, T> T acceptVisitor(TypedASTVisitor<S, T> visitor, S state) {
        return visitor.visit(state, this);
    }

    @Override
    public FileLocation getLocation() {
        return this.location;
    }

    public String toString() {
        return numerator.toString() + "/" + denominator.toString();
    }
}
