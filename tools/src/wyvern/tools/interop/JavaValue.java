package wyvern.tools.interop;

import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import wyvern.stdlib.support.Rational;
import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.effects.EffectAccumulator;
import wyvern.target.corewyvernIL.expression.AbstractValue;
import wyvern.target.corewyvernIL.expression.BooleanLiteral;
import wyvern.target.corewyvernIL.expression.RationalLiteral;
import wyvern.target.corewyvernIL.expression.CharacterLiteral;
import wyvern.target.corewyvernIL.expression.FloatLiteral;
import wyvern.target.corewyvernIL.expression.IntegerLiteral;
import wyvern.target.corewyvernIL.expression.Invokable;
import wyvern.target.corewyvernIL.expression.MethodCall;
import wyvern.target.corewyvernIL.expression.ObjectValue;
import wyvern.target.corewyvernIL.expression.StringLiteral;
import wyvern.target.corewyvernIL.expression.SuspendedTailCall;
import wyvern.target.corewyvernIL.expression.Value;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.support.Util;
import wyvern.target.corewyvernIL.type.StructuralType;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.ToolError;
import wyvern.tools.parsing.coreparser.ParseException;
import wyvern.tools.tests.TestUtil;

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
    public Value invoke(String methodName, List<Value> args, FileLocation loc) {
        List<Object> javaArgs = new LinkedList<Object>();
        Class<?>[] hints = foreignObject.getTypeHints(methodName);
        int hintNum = 0;
        for (Value arg : args) {
            Class<?> hintClass = (hints != null && hints.length > hintNum) ? hints[hintNum] : null;
            javaArgs.add(wyvernToJava(arg, hintClass));
            hintNum++;
        }
        Object result;
        try {
            result = foreignObject.invokeMethod(methodName, javaArgs);
            return javaToWyvern(result);
        } catch (ReflectiveOperationException e) {
            if (e instanceof InvocationTargetException) {
                Throwable cause = e.getCause();
                if (cause instanceof RuntimeException) {
                    throw (RuntimeException) cause;
                }
                if (cause instanceof ParseException) {
                    ParseException pe = (ParseException) cause;
                    ToolError.reportError(ErrorMessage.PARSE_ERROR, loc, pe.getMessage());
                }
            }
            throw new RuntimeException(e);
        }
    }

    /**
     * Only handles integers, strings, and null right now.
     * null turns into unit.
     */
    private Value javaToWyvern(Object result) {
        //if (result instanceof SuspendedTailCall) {
        //    result = MethodCall.trampoline((Value) result);
        //}
        if (result instanceof Integer) {
            return new IntegerLiteral((Integer) result);
        } else if (result instanceof Long) {
            return new IntegerLiteral(BigInteger.valueOf((Long) result));
        } else if (result instanceof Double) {
            return new FloatLiteral((Double) result);
        } else if (result instanceof String) {
            return new StringLiteral((String) result);
        } else if (result instanceof Character) {
            return new CharacterLiteral((Character) result);
        } else if (result == null) {
            // null is represented as unit
            return Util.nullValue();
        } else if (result instanceof List) {
            ObjectValue v = null;
            try {
                v = (ObjectValue) TestUtil.evaluate("import wyvern.collections.list\n"
                      + "list.makeD()\n");
                for (Object elem : (List<?>) result) {
                    List<Value> args = new LinkedList<>();
                    args.add(javaToWyvern(elem));
                    v.invoke("append", args);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return v;
        } else if (result instanceof Boolean) {
            return new BooleanLiteral((Boolean) result);
        } else if (result instanceof StructuralType) {
            return new JavaValue(JavaWrapper.wrapObject(result), Util.emptyType());
        } else if (result instanceof Value) {
            // Needed for returning arbitrary values from reflection's invoke.
            return (Value) result;
        } else if (result instanceof Rational) {
            return new RationalLiteral(((Rational) result).getNumerator(), ((Rational) result).getDenominator());
        } else {
            // return it as a unit; try to do better than this later
            return new JavaValue(JavaWrapper.wrapObject(result), Util.emptyType());
        }
    }

    /**
     * Only handles integers right now
     * @param hintClass
     */
    private Object wyvernToJava(Value arg, Class<?> hintClass) {
        if (arg instanceof IntegerLiteral) {
            if (hintClass != null && hintClass == BigInteger.class) {
                return ((IntegerLiteral) arg).getFullValue();
            }
            if (hintClass != null && hintClass == Long.class) {
                return ((IntegerLiteral) arg).getLongValue();
            }
            return (Integer) (((IntegerLiteral) arg).getValue());
        } else if (arg instanceof FloatLiteral) {
            if (hintClass != null && hintClass == Double.class) {
                return ((FloatLiteral) arg).getFullValue();
            }
            return ((FloatLiteral) arg).getFullValue();
        } else if (arg instanceof StringLiteral) {
            return new String(((StringLiteral) arg).getValue());
        } else if (arg instanceof CharacterLiteral) {
            return (Character) (((CharacterLiteral) arg).getValue());
        } else if (arg instanceof ObjectValue) {
            // Check if arg looks like a list type
            ObjectValue wyvObj = (ObjectValue) arg;
            // is it null?
            if (wyvObj.getSelfName().equals(Util.NULL_SELF)) {
                return null;
            }
            // is it a list?
            if (wyvObj.findDecl("get", false) != null && wyvObj.findDecl("length", false) != null) {
                List<Value> javaList = new LinkedList<>();
                int listLen = ((IntegerLiteral) (wyvObj.invoke("length", new LinkedList<>()))).getValue();
                for (int i = 0; i < listLen; i++) {
                    LinkedList<Value> args = new LinkedList<>();
                    args.add(new IntegerLiteral(i));
                    Value element = MethodCall.trampoline(wyvObj.invoke("get", args));
                    Value v = ((ObjectValue) element).getField("value");
                    javaList.add(v);
                }
                return javaList;
            }
            return arg;
        } else if (arg instanceof BooleanLiteral) {
            return (Boolean) (((BooleanLiteral) arg).getValue());
        } else if (arg instanceof JavaValue) {
            return ((JavaValue) arg).getWrappedValue();
        } else if (arg instanceof RationalLiteral) {
            return new Rational(((RationalLiteral) arg).getNumerator(), ((RationalLiteral) arg).getDenominator());
        } else if (arg instanceof SuspendedTailCall) {
                return wyvernToJava(MethodCall.trampoline(arg), hintClass);
        } else {
            // return arg;
            throw new RuntimeException("some Wyvern->Java cases not implemented");
        }
    }

    @Override
    public Value getField(String fieldName) {
        throw new RuntimeException("getting a Java object's field not implemented yet");
    }

    @Override
    public <S, T> T acceptVisitor(ASTVisitor<S, T> emitILVisitor, S state) {
        throw new RuntimeException("visiting a JavaValue is not defined");
    }

    @Override
    public ValueType typeCheck(TypeContext ctx, EffectAccumulator effectAccumulator) {
        return this.getType();
    }

    @Override
    public Set<String> getFreeVariables() {
        return new HashSet<>();
    }

    @Override
    public ValueType getType() {
        return this.getType();
    }

    public Object getWrappedValue() {
        return this.foreignObject.getWrappedValue();
    }
}
