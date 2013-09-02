package wyvern.tools.typedAST.extensions.interop.java.typedAST;

import wyvern.tools.errors.FileLocation;
import wyvern.tools.parsing.LineParser;
import wyvern.tools.parsing.LineSequenceParser;
import wyvern.tools.typedAST.core.binding.NameBinding;
import wyvern.tools.typedAST.extensions.interop.java.Util;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.util.TreeWriter;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;

public class JavaInvocation implements TypedAST {
	private final List<String> argNames;
	private final MethodHandle input;
	private Type type;
	private Class<?>[] parameterTypes;
	private boolean aStatic;
	private Class<?> declaringClass;

	public JavaInvocation(MethodHandle input, Method reflected, List<String> argNames) {
		this.argNames = argNames;
		this.input = input;
		type = Util.javaToWyvType(reflected.getReturnType());
		parameterTypes = reflected.getParameterTypes();
		aStatic = Modifier.isStatic(reflected.getModifiers());
		declaringClass = reflected.getDeclaringClass();
	}

	public JavaInvocation(MethodHandle mh, Constructor c, List<String> names) {
		this.argNames = names;
		this.input = mh;
		type = Util.javaToWyvType(c.getDeclaringClass());
		parameterTypes = c.getParameterTypes();
		aStatic = Modifier.isStatic(c.getModifiers());
		declaringClass = c.getDeclaringClass();
	}

	@Override
	public Type getType() {
		return type;
	}

	@Override
	public Type typecheck(Environment env) {
		for (String arg : argNames) {
			Type wyvType = env.lookup(arg).getType();

		}
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public Value evaluate(Environment env) {
		Object[] values = new Object[argNames.size()];
		int valIdx = 0;
		for (String argname : argNames) {
			values[valIdx] = Util.toJavaObject(env.getValue(argname), parameterTypes[valIdx++]);
		}
		Object receiver = null;
		if (!aStatic) {
			receiver = Util.toJavaObject(env.getValue("this"), declaringClass);
		}


		try {
			Object[] args = new Object[values.length+((receiver != null)?1:0)];
			if (receiver != null)
				args[0] = receiver;
			for (int i = 0; i < values.length; i++)
				args[i+((receiver != null)?1:0)] = values[i];

			return Util.toWyvObj(input.invokeWithArguments(args));  //To change body of implemented methods use File | Settings | File Templates.
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public LineParser getLineParser() {
		return null;
	}

	@Override
	public LineSequenceParser getLineSequenceParser() {
		return null;
	}

	@Override
	public FileLocation getLocation() {
		return FileLocation.UNKNOWN;
	}

	@Override
	public void writeArgsToTree(TreeWriter writer) {
	}
}
