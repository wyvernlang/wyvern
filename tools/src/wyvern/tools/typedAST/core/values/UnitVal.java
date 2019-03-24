package wyvern.tools.typedAST.core.values;

import java.util.List;

import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.modules.TypedModuleSpec;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.support.Util;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.typedAST.abs.AbstractExpressionAST;
import wyvern.tools.typedAST.interfaces.CoreAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.typedAST.typedastvisitor.TypedASTVisitor;


public final class UnitVal extends AbstractExpressionAST implements Value, CoreAST {
    private UnitVal(FileLocation location) {
        this.location = location;
    }
    // private static UnitVal instance = new UnitVal(); // FIXME: I have to move away from instance to provide line number! :(
    public static UnitVal getInstance(FileLocation fileLocation) {
        return new UnitVal(fileLocation); // instance;
    }

    @Override
    public String toString() {
        return "()";
    }

    private FileLocation location;
    public FileLocation getLocation() {
        return this.location;
    }
    @Override
    public Expression generateIL(GenContext ctx, ValueType expectedType, List<TypedModuleSpec> dependencies) {
        return Util.unitValue();
    }

    @Override
    public StringBuilder prettyPrint() {
        StringBuilder sb = new StringBuilder();
        sb.append("()");
        return sb;
    }

    @Override
    public <S, T> T acceptVisitor(TypedASTVisitor<S, T> visitor, S state) {
        return visitor.visit(state, this);
    }

}
