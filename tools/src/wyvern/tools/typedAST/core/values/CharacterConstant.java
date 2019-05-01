package wyvern.tools.typedAST.core.values;

import java.util.List;

import wyvern.target.corewyvernIL.expression.CharacterLiteral;
import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.modules.TypedModuleSpec;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.typedAST.abs.AbstractExpressionAST;
import wyvern.tools.typedAST.interfaces.CoreAST;
import wyvern.tools.typedAST.interfaces.InvokableValue;
import wyvern.tools.typedAST.typedastvisitor.TypedASTVisitor;

public class CharacterConstant extends AbstractExpressionAST implements InvokableValue, CoreAST {
    private char value;

    public CharacterConstant(char c) {
        this.value = c;
    }
    public CharacterConstant(char c, FileLocation loc) {
        this.value = c; location = loc;
    }

    public char getValue() {
        return value;
    }


    private FileLocation location = FileLocation.UNKNOWN;
    public FileLocation getLocation() {
        return this.location;
    }

    @Override
    public Expression generateIL(GenContext ctx, ValueType expectedType, List<TypedModuleSpec> dependencies) {
        return new CharacterLiteral(value, location);
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
