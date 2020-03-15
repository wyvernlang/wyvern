package wyvern.tools.typedAST.abs;

import java.util.LinkedList;

import wyvern.target.corewyvernIL.modules.TypedModuleSpec;
import wyvern.target.corewyvernIL.support.CallableExprGenerator;
import wyvern.target.corewyvernIL.support.DefaultExprGenerator;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.tools.typedAST.interfaces.ExpressionAST;
import wyvern.tools.util.AbstractTreeWritable;

public abstract class AbstractExpressionAST extends AbstractTreeWritable implements ExpressionAST {
    @Override
    public CallableExprGenerator getCallableExpr(GenContext ctx) {
        return new DefaultExprGenerator(generateIL(ctx, null, new LinkedList<TypedModuleSpec>()));
    }
    @Override
    public String toString() {
        return prettyPrint().toString();
    }
}
