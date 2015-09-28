package wyvern.target.corewyvernIL.expression;

import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.type.ValueType;

public interface IExpr {
	public ValueType typeCheck(TypeContext ctx);
}
