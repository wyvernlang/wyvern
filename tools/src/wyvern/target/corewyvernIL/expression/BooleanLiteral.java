package wyvern.target.corewyvernIL.expression;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.support.EvalContext;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.support.Util;
import wyvern.target.corewyvernIL.type.ValueType;

public class BooleanLiteral extends Literal implements Invokable {

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (value ? 1231 : 1237);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        BooleanLiteral other = (BooleanLiteral) obj;
        if (value != other.value)
            return false;
        return true;
    }

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
					return new SuspendedTailCall(this.getExprType(), this.getLocation()) {
						@Override public Value interpret(EvalContext ignored) {
							return ((ObjectValue) args.get(0)).invoke("apply", new ArrayList<>());
						}
					};
				}
				return new SuspendedTailCall(this.getExprType(), this.getLocation()) {
					@Override public Value interpret(EvalContext ignored) {
						return ((ObjectValue) args.get(1)).invoke("apply", new ArrayList<>());
					}
				};
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

