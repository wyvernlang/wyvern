package wyvern.target.corewyvernIL.expression;

import java.util.List;

public interface Invokable extends Value {
	Value invoke(String methodName, List<Value> args);
	Value getField(String fieldName);
}
