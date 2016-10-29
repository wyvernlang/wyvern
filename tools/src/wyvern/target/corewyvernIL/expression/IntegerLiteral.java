package wyvern.target.corewyvernIL.expression;

import java.io.IOException;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import wyvern.target.corewyvernIL.Environment;
import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.support.Util;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.target.oir.OIREnvironment;
import wyvern.tools.errors.FileLocation;

public class IntegerLiteral extends Literal implements Invokable {

	private final BigInteger value;

	@Override
	public int hashCode() {
		return value.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		IntegerLiteral other = (IntegerLiteral) obj;
		if (!value.equals(other.value))
			return false;
		return true;
	}

	public IntegerLiteral(int value) {
		this(value, FileLocation.UNKNOWN);
	}
	public IntegerLiteral(BigInteger value) {
		this(value, FileLocation.UNKNOWN);
	}
	public IntegerLiteral(int value, FileLocation loc) {
		this(BigInteger.valueOf(value), loc);
	}
	public IntegerLiteral(BigInteger value, FileLocation loc) {
		super(Util.intType(), loc);
		this.value = value;
	}


	@Override
	public void doPrettyPrint(Appendable dest, String indent) throws IOException {
		dest.append(value.toString());
	}

	public int getValue() {
		return value.intValueExact();
	}

	public BigInteger getFullValue() {
		return value;
	}

	@Override
	public ValueType typeCheck(TypeContext env) {
		return Util.intType();
	}

	@Override
	public <S, T> T acceptVisitor(ASTVisitor <S, T> emitILVisitor,
			S state) {
		return emitILVisitor.visit(state, this);
		//throw new RuntimeException("not implemented");
	}
	
	@Override
	public Set<String> getFreeVariables() {
		return new HashSet<>();
	}


	@Override
	public ValueType getType() {
		return Util.intType();
	}

	@Override
	public Value invoke(String methodName, List<Value> args) {

		switch (methodName) {
		case "+": return new IntegerLiteral(this.value.add(((IntegerLiteral)args.get(0)).getFullValue()));
		case "-": return new IntegerLiteral(this.value.subtract(((IntegerLiteral)args.get(0)).getFullValue()));
		case "*": return new IntegerLiteral(this.value.multiply(((IntegerLiteral)args.get(0)).getFullValue()));
		case "/": return new IntegerLiteral(this.value.divide(((IntegerLiteral)args.get(0)).getFullValue()));
		case "%": return new IntegerLiteral(this.value.remainder(((IntegerLiteral)args.get(0)).getFullValue()));
    case "negate": return new IntegerLiteral(this.value.negate());
		case "<": return new BooleanLiteral(this.value.compareTo(((IntegerLiteral)args.get(0)).getFullValue()) < 0);
		case ">": return new BooleanLiteral(this.value.compareTo(((IntegerLiteral)args.get(0)).getFullValue()) > 0);
    case "==": return new BooleanLiteral(this.value.compareTo(((IntegerLiteral)args.get(0)).getFullValue()) == 0);
		default: throw new RuntimeException("runtime error: integer operation " + methodName + "not supported by the runtime");
		}
	}

	@Override
	public Value getField(String fieldName) {
		throw new RuntimeException("no fields");
	}
}
