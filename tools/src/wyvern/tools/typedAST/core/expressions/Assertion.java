package wyvern.tools.typedAST.core.expressions;

import java.util.List;

import wyvern.target.corewyvernIL.expression.IExpr;
import wyvern.target.corewyvernIL.modules.TypedModuleSpec;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.support.ILFactory;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.typedAST.abs.AbstractExpressionAST;
import wyvern.tools.typedAST.interfaces.ExpressionAST;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.typedastvisitor.TypedASTVisitor;

public class Assertion extends AbstractExpressionAST {
    private FileLocation location;
    private ExpressionAST expression;
    private String description;

    public Assertion(String description, TypedAST exp, FileLocation loc) {
        this.location = loc;
        this.description = description;
        this.expression = (ExpressionAST) exp;
    }

    @Override
    public IExpr generateIL(GenContext ctx, ValueType expectedType, List<TypedModuleSpec> dependencies) {
        IExpr newExp = expression.generateIL(ctx, expectedType, dependencies);
        ILFactory f = ILFactory.instance();

        return f.module("wyvern.runtime").call("assertion", f.string(description == null ? "" : description), newExp);
    }

    @Override
    public FileLocation getLocation() {
        return location;
    }

    @Override
    public <S, T> T acceptVisitor(TypedASTVisitor<S, T> visitor, S state) {
        return visitor.visit(state, this);
    }


}
