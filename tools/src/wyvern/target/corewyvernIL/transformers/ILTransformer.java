package wyvern.target.corewyvernIL.transformers;

import wyvern.target.corewyvernIL.expression.IExpr;

public interface ILTransformer {

	public IExpr transform(IExpr ast);
	
}
