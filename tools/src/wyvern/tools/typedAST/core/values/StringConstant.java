package wyvern.tools.typedAST.core.values;

import java.util.List;

import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.expression.StringLiteral;
import wyvern.target.corewyvernIL.modules.TypedModuleSpec;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.typedAST.interfaces.CoreAST;
import wyvern.tools.typedAST.interfaces.InvokableValue;
import wyvern.tools.typedAST.abs.AbstractExpressionAST;
import wyvern.tools.typedAST.typedastvisitor.TypedASTVisitor;

public class StringConstant extends AbstractExpressionAST implements InvokableValue, CoreAST {
    private String value;

    public StringConstant(String s) {
        this.value = s;
    }
    public StringConstant(String s, FileLocation loc) {
        this.value = s; location = loc;
    }

    public String getValue() {
        return value;
    }


    private FileLocation location = FileLocation.UNKNOWN;
    public FileLocation getLocation() {
        return this.location;
    }

    @Override
    public Expression generateIL(GenContext ctx, ValueType expectedType, List<TypedModuleSpec> dependencies) {
        return new StringLiteral(value, location);
    }

    public StringBuilder prettyPrint() {
        StringBuilder sb = new StringBuilder();
        sb.append("StringConstant(");
        sb.append(value);
        sb.append(")");
        return sb;
    }

    @Override
    public <S, T> T acceptVisitor(TypedASTVisitor<S, T> visitor, S state) {
        return visitor.visit(state, this);
    }

}
