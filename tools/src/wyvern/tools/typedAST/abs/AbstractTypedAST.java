package wyvern.tools.typedAST.abs;

import wyvern.target.corewyvernIL.support.CallableExprGenerator;
import wyvern.target.corewyvernIL.support.DefaultExprGenerator;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.util.AbstractTreeWritable;

public abstract class AbstractTypedAST extends AbstractTreeWritable implements TypedAST {
	@Override
	public CallableExprGenerator getCallableExpr(GenContext ctx) {
		return new DefaultExprGenerator(generateIL(ctx));
	}
}
