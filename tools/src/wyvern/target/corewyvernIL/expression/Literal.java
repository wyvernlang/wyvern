package wyvern.target.corewyvernIL.expression;

import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.FileLocation;

public abstract class Literal extends AbstractValue {

    protected Literal(ValueType exprType, FileLocation loc) {
        super(exprType, loc);
    }
}
