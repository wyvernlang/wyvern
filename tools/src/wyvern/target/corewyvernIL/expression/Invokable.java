package wyvern.target.corewyvernIL.expression;

import java.util.List;

import wyvern.target.corewyvernIL.support.EvalContext;

public interface Invokable extends Value {
	Value invoke(String methodName, List<Value> args, EvalContext ctx);
	Value getField(String fieldName, EvalContext ctx);
}
