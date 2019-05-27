package wyvern.tools.interop;

import java.lang.reflect.Method;
import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;

public class JObject implements FObject {
  private Object jObject;

  JObject(Object wrapped) {
    jObject = wrapped;
  }

  @Override
  public Object invokeMethod(String methodName, List<Object> args)
      throws ReflectiveOperationException {
    Class<?>[] parameterTypes = new Class<?>[args.size()];
    if (args.size() > 0) {
      int i = 0;
      for (Object arg : args) {
        // handle null objects by representing their type as null
        parameterTypes[i++] = (arg == null) ? null : arg.getClass();
      }
    }
    Class<?> cls = jObject.getClass();
    Method[] methods = cls.getMethods();
    List<Method> candidates = new LinkedList<Method>();
    Method bestMethod = null;
    for (int i = 0; i < methods.length; ++i) {
      Method m = methods[i];
      if (m.getName().equals(methodName)) {
        if (methodName.equals("setNum")) {
            System.out.println();
        }
        if (isApplicable(m, parameterTypes)) {
          if (bestMethod == null || isMorePrecise(m, bestMethod)) {
            bestMethod = m;
          }
          candidates.add(m);
        }
      }
    }
    if (bestMethod != null) {
      Object[] argArray = args.toArray();
      adapt(argArray, bestMethod.getParameterTypes());
      Object result = bestMethod.invoke(jObject, argArray);
      return result;
    } else {
      throw new RuntimeException("no applicable method '" + methodName + "'!");
    }
  }

  private void adapt(Object[] argArray, Class<?>[] parameterTypes) {
      for (int i = 0; i < argArray.length; ++i) {
          if (argArray[i] instanceof BigInteger && parameterTypes[i] == int.class) {
              argArray[i] = ((BigInteger) argArray[i]).intValue();
          }
          if (argArray[i] instanceof BigInteger && parameterTypes[i] == long.class) {
              argArray[i] = ((BigInteger) argArray[i]).longValue();
          }
      }
}

private boolean isMorePrecise(Method m, Method bestMethod) {
    Class<?>[] mFormalTypes = m.getParameterTypes();
    Class<?>[] bFormalTypes = bestMethod.getParameterTypes();
    for (int i = 0; i < mFormalTypes.length; ++i) {
      if (!bFormalTypes[i].isAssignableFrom(mFormalTypes[i])) {
        return false;
      }
    }
    return true;
  }

  private boolean isApplicable(Method m, Class<?>[] parameterTypes) {
    Class<?>[] formalTypes = m.getParameterTypes();
    if (formalTypes.length != parameterTypes.length) {
      return false;
    }
    for (int i = 0; i < formalTypes.length; ++i) {
      // null for the type means the object is null, so always assignable
      if (parameterTypes[i] != null && !isAssignableFrom(mapPrimitives(formalTypes[i]), parameterTypes[i])) {
        return false;
      }
    }
    return true;
  }

  private boolean isAssignableFrom(Class<?> c1, Class<?> c2) {
    if (c1.isAssignableFrom(c2)) {
      return true;
    }
    // handle numeric hierarchy
    if (c1 == Long.class && c2 == Integer.class) {
        return true;
    }
    // allow BigInteger to be assigned to Integer and Long - and hope we don't get overflow
    if (c2 == BigInteger.class && (c1 == Integer.class || c1 == Long.class)) {
        return true;
    }
    // default
    return false;
  }

  private Class<?> mapPrimitives(Class<?> class1) {
    if (class1.isPrimitive()) {
      // boolean, byte, char, short, int, long, float, and double.
      if (class1 == boolean.class) {
        return Boolean.class;
      } else if (class1 == byte.class) {
        return Byte.class;
      } else if (class1 == char.class) {
        return Character.class;
      } else if (class1 == short.class) {
        return Short.class;
      } else if (class1 == int.class) {
        return Integer.class;
      } else if (class1 == long.class) {
        return Long.class;
      } else if (class1 == float.class) {
        return Float.class;
      } else if (class1 == double.class) {
        return Double.class;
      } else {
        throw new RuntimeException("mapping not defined for " + class1);
      }
    } else {
      return class1;
    }
  }

  @Override
  public Class<?>[] getTypeHints(String methodName) {
    for (Method m : jObject.getClass().getMethods()) {
      if (m.getName().equals(methodName)) {
        return m.getParameterTypes();
      }
    }
    return null;
  }

  @Override
  public Class<?> getJavaClass() {
    return jObject.getClass();
  }

  @Override
  public Object getWrappedValue() {
    return jObject;
  }

}
