package wyvern.target.corewyvernIL.expression;

import wyvern.target.corewyvernIL.Environment;
import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.support.EvalContext;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.target.oir.OIREnvironment;

public class BooleanLiteral extends AbstractValue {

    private boolean value;

    public BooleanLiteral(boolean value) {
        super(null);
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }

    @Override
    public ValueType typeCheck(TypeContext env) {
        // TODO Auto-generated method stub
        return null;
    }

	@Override
	public <T> T acceptVisitor(ASTVisitor<T> emitILVisitor, Environment env,
			OIREnvironment oirenv) {
		// TODO Auto-generated method stub
		return null;
	}
}

