package wyvern.target.corewyvernIL.expression;

import java.util.List;

public interface Invokable extends Value {
	/** Invokes a method on this Invokable.
	 * 
	 * @param methodName
	 * @param args
	 * @return The result of evaluation, or a SuspendedTailCall thunk for continuing evaluation of a tail call
	 */
	Value invoke(String methodName, List<Value> args);
	Value getField(String fieldName);
}
