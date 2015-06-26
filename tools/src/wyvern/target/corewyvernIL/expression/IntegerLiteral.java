package wyvern.target.corewyvernIL.expression;

import wyvern.target.corewyvernIL.Environment;
import wyvern.target.corewyvernIL.astvisitor.EmitILVisitor;
import wyvern.target.corewyvernIL.type.ValueType;

public class IntegerLiteral extends Literal implements Value{

    private int value;

    public IntegerLiteral(int value) {
        super();
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public java.lang.String acceptEmitILVisitor(EmitILVisitor emitILVisitor,
                                                Environment env) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ValueType typeCheck(wyvern.tools.types.Environment env) {
        // TODO Auto-generated method stub
        return null;
    }
}
