package wyvern.tools.typedAST.extensions.interop.java.typedAST;

import wyvern.tools.errors.FileLocation;
import wyvern.tools.typedAST.abs.AbstractValue;
import wyvern.tools.typedAST.core.Application;
import wyvern.tools.typedAST.core.Closure;
import wyvern.tools.typedAST.core.binding.NameBinding;
import wyvern.tools.typedAST.core.binding.TypeBinding;
import wyvern.tools.typedAST.core.values.TupleValue;
import wyvern.tools.typedAST.extensions.interop.java.Util;
import wyvern.tools.typedAST.interfaces.ApplyableValue;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.Tuple;
import wyvern.tools.types.extensions.Unit;
import wyvern.tools.util.Pair;
import wyvern.tools.util.TreeWriter;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.*;

public class JClosure extends AbstractValue implements ApplyableValue {
	public static class JavaInvokableMethod {
		private final Class[] paramaterTypes;
		private Boolean classMeth;
		private final MethodHandle handle;
		private final Type returnType;
		private final List<String> paramNames;
		private Class srcClass;

		public JavaInvokableMethod(Class[] parameterTypes, Class ret, MethodHandle handle, List<String> paramNames, Boolean classMeth, Class source) {
			this.handle = handle;
			this.paramNames = paramNames;
			this.paramaterTypes = parameterTypes;
			this.classMeth = classMeth;
			returnType = Util.javaToWyvType(ret);
			this.srcClass = source;
		}

		public MethodHandle getHandle() {
			return handle;
		}

		public List<String> getParamNames() {
			return paramNames;
		}

		public Class[] getParameterTypes() {
			return paramaterTypes;
		}

		public Boolean getClassMeth() {
			return classMeth;
		}

		public Class getSrcClass() {
			return srcClass;
		}

		public Type getReturnType() {
			return returnType;
		}
	}

	List<JavaInvokableMethod> methods = new ArrayList<>();
	private Environment evalEnv;

	public JClosure(List<JavaInvokableMethod> methods, Environment evalEnv) {
		this.methods = methods;
		this.evalEnv = evalEnv;
		Collections.sort(this.methods, new Comparator<JavaInvokableMethod>() {
			@Override
			public int compare(JavaInvokableMethod o1, JavaInvokableMethod o2) {
				if (o1.getParameterTypes().length > o2.getParameterTypes().length)
					return 1;
				else if (o1.getParameterTypes().length < o2.getParameterTypes().length)
					return -1;

				Class[] args1 = o1.getParameterTypes();
				Class[] args2 = o2.getParameterTypes();
				for (int i = 0; i < args1.length; i++) {
					if (!args2[i].isAssignableFrom(args1[i])) {
						return 1;
					}
				}
				return -1;
			}
		});
	}

	private Type[] fromValue(Value value) {
		if (value.getType() instanceof Tuple) {
			return ((Tuple) value.getType()).getTypes();
		} else {
			return new Type[] { value.getType() };
		}
	}

	private Value[] vFromV(Value value) {
		if (value instanceof TupleValue) {
			return ((TupleValue)value).getValues();
		} else {
			return new Value[] { value };
		}
	}


	@Override
	public Value evaluateApplication(Application app, Environment env) {
		Environment iEnv = evalEnv.extend(env);
		//Implementation of section 15.12.2.2. of the Java Language Specification
		for (JavaInvokableMethod m : methods) {
			boolean suitable = true;
			Class<?>[] parameterTypes = m.getParameterTypes();
			Type[] wyvTypes = new Type[parameterTypes.length];
			Value[] values = vFromV(app.getArgument().evaluate(env));
			for (int i = 0; i < parameterTypes.length; i++) {

				Type wyv = values[i].getType();
				wyvTypes[i] = wyv;

				if (!Util.checkTypeCast(wyv, parameterTypes[i])) {
					suitable = false;
					break;
				}
			}
			if (!suitable)
				continue;
			//This is the first method => most specific
			Value evaluate = app.getArgument().evaluate(iEnv);
			Value[] args = values;
			Type[] sig = fromValue(evaluate);
			if (sig.length == 1 && sig[0] instanceof Unit) {
				sig = new Type[] {};
			}
			if (sig.length != wyvTypes.length)
				continue;

			for (int i = 0; i < wyvTypes.length; i++) {
				if (!(sig[i].subtype(wyvTypes[i]))) {
					suitable = false;
					break;
				}
			}
			if (!suitable)
				continue;

			Object[] jArgs;
			if (m.classMeth) {
				jArgs = new Object[parameterTypes.length];
				for (int i = 0; i < parameterTypes.length; i++)
					jArgs[i] = Util.toJavaObject(args[i], parameterTypes[i]);
			} else {
				jArgs = new Object[parameterTypes.length + 1];
				jArgs[0] = Util.toJavaObject(evalEnv.getValue("this"), m.getSrcClass());
				for (int i = 0; i < parameterTypes.length; i++)
					jArgs[i+1] = Util.toJavaObject(args[i], parameterTypes[i]);
			}

			try {
				return Util.toWyvObj(m.getHandle().invokeWithArguments(jArgs));
			} catch (Throwable throwable) {
				throw new RuntimeException(throwable);
			}
		}
		throw new RuntimeException();
	}


	@Override
	public Type getType() {
		return null;
	}

	@Override
	public Map<String, TypedAST> getChildren() {
		return new HashMap<>();
	}

	@Override
	public TypedAST cloneWithChildren(Map<String, TypedAST> newChildren) {
		return this;
	}

	@Override
	public FileLocation getLocation() {
		return null;
	}

	@Override
	public void writeArgsToTree(TreeWriter writer) {
	}
}
