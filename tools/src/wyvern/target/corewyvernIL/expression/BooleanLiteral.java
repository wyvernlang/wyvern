package wyvern.target.corewyvernIL.expression;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import wyvern.target.corewyvernIL.Environment;
import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.support.Util;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.target.oir.OIREnvironment;

public class BooleanLiteral extends Literal implements Invokable {

    private boolean value;

    public BooleanLiteral(boolean value) {
        super(Util.booleanType(), null);
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
	public <S, T> T acceptVisitor(ASTVisitor<S, T> emitILVisitor, S state) {
      return emitILVisitor.visit(state, this);
	}

	@Override
	public Set<String> getFreeVariables() {
		return new HashSet<>();
	}

	@Override
	public ValueType getType() {
		return Util.booleanType();
	}

	@Override
	public Value invoke(String methodName, List<Value> args) {
		switch (methodName) {
			case "ifTrue":
				if (this.value) {
					return ((ObjectValue) args.get(0)).invoke("apply", new ArrayList<>());
				}
				return ((ObjectValue) args.get(1)).invoke("apply", new ArrayList<>());
      case "&&":
        return new BooleanLiteral(this.value && ((BooleanLiteral) args.get(0)).value);
      case "||":
        return new BooleanLiteral(this.value || ((BooleanLiteral) args.get(0)).value);
			default: throw new RuntimeException();
		}
	}

	@Override
	public Value getField(String fieldName) {
		throw new RuntimeException("no fields");
	}
}

