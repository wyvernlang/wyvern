package wyvern.tools.typedAST.core.values;

import java.math.BigInteger;
import java.util.List;

import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.expression.IntegerLiteral;
import wyvern.target.corewyvernIL.modules.TypedModuleSpec;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.typedAST.abs.AbstractExpressionAST;
import wyvern.tools.typedAST.interfaces.CoreAST;
import wyvern.tools.typedAST.interfaces.InvokableValue;
import wyvern.tools.typedAST.typedastvisitor.TypedASTVisitor;

public class IntegerConstant extends AbstractExpressionAST implements InvokableValue, CoreAST {
    private BigInteger value;

    public IntegerConstant(BigInteger i, FileLocation loc) {
        value = i;
        location = loc;
    }

    public BigInteger getValue() {
        return value;
    }

    private FileLocation location = FileLocation.UNKNOWN;
    public FileLocation getLocation() {
        return this.location;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof IntegerConstant)) {
            return false;
        }
        if (((IntegerConstant) o).getValue() != this.getValue()) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public Expression generateIL(GenContext ctx, ValueType expectedType, List<TypedModuleSpec> dependencies) {
        return new IntegerLiteral(value, location);
    }

    @Override
    public <S, T> T acceptVisitor(TypedASTVisitor<S, T> visitor, S state) {
        return visitor.visit(state, this);
    }

}
