package wyvern.target.corewyvernIL.expression;

import wyvern.target.corewyvernIL.support.EvalContext;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.FileLocation;

public abstract class AbstractValue extends Expression implements Value {
    protected AbstractValue(ValueType exprType, FileLocation loc) {
        super(exprType, loc);
    }

    @Override
    public Value interpret(EvalContext ctx) {
        return this;
    }
}
