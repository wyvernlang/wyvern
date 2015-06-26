package wyvern.target.corewyvernIL.expression;

import wyvern.target.corewyvernIL.Environment;
import wyvern.target.corewyvernIL.astvisitor.EmitILVisitor;
import wyvern.target.corewyvernIL.type.ValueType;

public class BooleanLiteral extends Literal implements Value{

    private boolean value;

    public BooleanLiteral(boolean value) {
        super();
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }

    public void setValue(boolean value) {
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

