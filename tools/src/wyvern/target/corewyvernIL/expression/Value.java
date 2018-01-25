package wyvern.target.corewyvernIL.expression;

import wyvern.target.corewyvernIL.type.ValueType;

// TODO: rename this "ValueOrThunk" and create a subclass that is just a value

public interface Value extends IExpr {
    ValueType getType();

    default Value executeIfThunk() {
        return MethodCall.trampoline(this); // this.interpret(null);
    }
}
