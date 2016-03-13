package wyvern.target.corewyvernIL.expression;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import wyvern.target.corewyvernIL.Environment;
import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.support.Util;
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

	@Override
	public void doPrettyPrint(Appendable dest, String indent) throws IOException {
		dest.append(value?"true":"false");
	}

    @Override
    public ValueType typeCheck(TypeContext env) {
        return Util.booleanType();
    }

	@Override
	public <T> T acceptVisitor(ASTVisitor<T> emitILVisitor, Environment env,
			OIREnvironment oirenv) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> getFreeVariables() {
		return new HashSet<>();
	}

	@Override
	public ValueType getType() {
		return Util.booleanType();
	}
}

