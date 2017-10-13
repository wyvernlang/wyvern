package wyvern.tools.typedAST.abs;

import java.util.LinkedList;
import java.util.Optional;

import wyvern.target.corewyvernIL.modules.TypedModuleSpec;
import wyvern.target.corewyvernIL.support.CallableExprGenerator;
import wyvern.target.corewyvernIL.support.DefaultExprGenerator;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.tools.typedAST.interfaces.ExpressionAST;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.util.AbstractTreeWritable;

public abstract class AbstractExpressionAST extends AbstractTreeWritable implements ExpressionAST {
	@Override
	public CallableExprGenerator getCallableExpr(GenContext ctx) {
		return new DefaultExprGenerator(generateIL(ctx, null, new LinkedList<TypedModuleSpec>()));
	}
    @Override
    @Deprecated
    public Type typecheck(Environment env, Optional<Type> expected) {
        throw new RuntimeException();
    }
}
