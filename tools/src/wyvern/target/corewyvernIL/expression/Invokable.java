package wyvern.target.corewyvernIL.expression;

import java.util.List;

import wyvern.tools.errors.FileLocation;

public interface Invokable extends Value {
    /** Invokes a method on this Invokable.
     *
     * @param methodName
     * @param args
     * @return The result of evaluation, or a SuspendedTailCall thunk for continuing evaluation of a tail call
     */
    Value invoke(String methodName, List<Value> args, FileLocation loc);
    default Value invoke(String methodName, List<Value> args) {
        return invoke(methodName, args, null);
    }
    Value getField(String fieldName);
}
