package wyvern.tools.typedAST.interfaces;

import java.util.List;

import wyvern.target.corewyvernIL.expression.IExpr;
import wyvern.target.corewyvernIL.modules.TypedModuleSpec;
import wyvern.target.corewyvernIL.support.CallableExprGenerator;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.support.TopLevelContext;
import wyvern.target.corewyvernIL.type.ValueType;

public interface ExpressionAST extends TypedAST {

    /** Generates the new Wyvern Intermediate Language (IL) from this
     * AST.
     *
     * @param ctx The generation context used for translation
     * @param expectedType TODO
     * @param dependencies TODO
     * @return
     */
    IExpr generateIL(GenContext ctx, ValueType expectedType, List<TypedModuleSpec> dependencies);

    CallableExprGenerator getCallableExpr(GenContext ctx);

    @Override
    default void genTopLevel(TopLevelContext topLevelContext) {
        final IExpr exp = generateIL(topLevelContext.getContext(), null, topLevelContext.getDependencies());
        ValueType type = exp.typeCheck(topLevelContext.getContext(), null);
        topLevelContext.addExpression(exp, type);
    }
}
