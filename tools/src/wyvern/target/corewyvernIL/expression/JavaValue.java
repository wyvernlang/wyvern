package wyvern.target.corewyvernIL.expression;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import wyvern.target.corewyvernIL.Environment;
import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.target.oir.OIREnvironment;
import wyvern.tools.interop.FObject;

public class JavaValue extends AbstractValue implements Invokable {
	// FObject is part of a non-Wyvern-specific Java interop library
	// e.g. it could be re-used by Plaid or some future language design
	private FObject foreignObject;

	public JavaValue(FObject foreignObject, ValueType exprType) {
		super(exprType);
		this.foreignObject = foreignObject;
	}

	@Override
	public Value invoke(String methodName, List<Value> args) {
		List<Object> javaArgs = new LinkedList<Object>();
		for (Value arg : args) {
			javaArgs.add(wyvernToJava(arg));
		}
		Object result;
		try {
			result = foreignObject.invokeMethod(methodName, javaArgs);
			return javaToWyvern(result);
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException(e);
		}
	}
	/**
	 * Only handles integers right now
	 */
	private Value javaToWyvern(Object result) {
		if (result instanceof Integer) {
			return new IntegerLiteral((Integer)result);
        } else if(result instanceof String) {
            return new StringLiteral((String) result);
        } else {
			throw new RuntimeException("some Java->Wyvern cases not implemented");
		}
	}

	/**
	 * Only handles integers right now
	 */
	private Object wyvernToJava(Value arg) {
		if (arg instanceof IntegerLiteral) {
			return new Integer(((IntegerLiteral)arg).getValue());
        } else if (arg instanceof StringLiteral) {
            return new String(((StringLiteral) arg).getValue());
		} else {
			throw new RuntimeException("some Wyvern->Java cases not implemented");
		}
	}

	@Override
	public Value getField(String fieldName) {
		throw new RuntimeException("getting a Java object's field not implemented yet");
	}

	@Override
	public <T> T acceptVisitor(ASTVisitor<T> emitILVisitor, Environment env, OIREnvironment oirenv) {
		throw new RuntimeException("visiting a JavaValue is not defined");
	}

	@Override
	public ValueType typeCheck(TypeContext ctx) {
		return this.getExprType();
	}

	@Override
	public Set<String> getFreeVariables() {
		return new HashSet<>();
	}

	@Override
	public ValueType getType() {
		return this.getExprType();
	}
}
