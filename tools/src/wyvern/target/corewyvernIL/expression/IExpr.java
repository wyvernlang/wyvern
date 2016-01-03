package wyvern.target.corewyvernIL.expression;

import java.io.IOException;

import wyvern.target.corewyvernIL.support.EvalContext;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.type.ValueType;

public interface IExpr {
	public ValueType typeCheck(TypeContext ctx);
	public abstract Value interpret(EvalContext ctx);
	void doPrettyPrint(Appendable dest, String indent) throws IOException;
}
