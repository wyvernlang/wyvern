package wyvern.target.corewyvernIL.expression;

import java.util.*;

import wyvern.target.corewyvernIL.Environment;
import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.support.Util;
import wyvern.target.corewyvernIL.type.NominalType;
import wyvern.target.corewyvernIL.type.StructuralType;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.target.oir.OIREnvironment;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.interop.*;

public class JavaValue extends AbstractValue implements Invokable {
	// FObject is part of a non-Wyvern-specific Java interop library
	// e.g. it could be re-used by Plaid or some future language design
	private FObject foreignObject;

	public JavaValue(FObject foreignObject, ValueType exprType) {
		super(exprType, FileLocation.UNKNOWN);
		this.foreignObject = foreignObject;
	}

	public FObject getFObject() {
		return this.foreignObject;
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
	 * Only handles integers, strings, and null right now.
	 * null turns into unit.
	 */
	private Value javaToWyvern(Object result) {
		if (result instanceof Integer) {
			return new IntegerLiteral((Integer)result);
        } else if(result instanceof String) {
            return new StringLiteral((String) result);
        } else if(result == null) {
            return Util.unitValue();
        } else if(result instanceof List) {
			return new JavaValue(JavaWrapper.wrapObject(result), new NominalType("system", "List"));
		} else if(result instanceof Boolean) {
			return new BooleanLiteral((Boolean) result);
		} else if(result instanceof StructuralType) {
			return new JavaValue(JavaWrapper.wrapObject(result), Util.emptyType());
		} else if(result instanceof Value) {
			// Needed for returning arbitrary values from reflection's invoke.
			return (Value) result;
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
		} else if (arg instanceof ObjectValue) {
			List<Value> emptyList = new LinkedList<>();
			// Extremely hacky. Won't work if a different list implementation is used, for example.
			if (arg.getType() instanceof NominalType) {
				if (((NominalType) arg.getType()).getTypeMember().equals("List")) {
					ObjectValue wyvernArgList = (ObjectValue) arg;
					List<Value> argList = new ArrayList<>();
					while (((IntegerLiteral) (wyvernArgList.getField("length"))).getValue() != 0) {
						argList.add(((ObjectValue) arg).invoke("getVal", emptyList));
						wyvernArgList = (ObjectValue) wyvernArgList.invoke("getNext", emptyList);
					}
					return argList;
				}
			}
			return arg;
		} else if (arg instanceof BooleanLiteral){
			return new Boolean(((BooleanLiteral)arg).getValue());
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
