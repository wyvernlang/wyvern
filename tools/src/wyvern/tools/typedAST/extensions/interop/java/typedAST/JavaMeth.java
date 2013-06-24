package wyvern.tools.typedAST.extensions.interop.java.typedAST;


import wyvern.tools.errors.FileLocation;
import wyvern.tools.typedAST.core.binding.NameBinding;
import wyvern.tools.typedAST.core.binding.NameBindingImpl;
import wyvern.tools.typedAST.core.declarations.FunDeclaration;
import wyvern.tools.typedAST.extensions.interop.java.Util;
import wyvern.tools.typedAST.extensions.interop.java.types.JavaClassType;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.types.Type;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class JavaMeth extends FunDeclaration {
	private static List<String> getNames(Method m) {
		Class[] args = m.getParameterTypes();
		ArrayList<String> output = new ArrayList<String>();
		for (int i = 0; i < args.length; i++)
			output.add("arg"+i);
		return output;
	}

	private static List<NameBinding> getNameBindings(Method m) {
		Class[] args = m.getParameterTypes();
		List<String> names = getNames(m);
		ArrayList<NameBinding> output = new ArrayList<NameBinding>();
		int i = 0;
		for (Class arg : args) {
			output.add(new NameBindingImpl(names.get(i++), Util.javaToWyvType(arg)));
		}
		return output;

	}

	public JavaMeth(MethodHandle mh, Method m) {
		super(m.getName(),
				getNameBindings(m),
				Util.javaToWyvType(m.getReturnType()),
				new JavaInvocation(mh, m, getNames(m)),
				Modifier.isStatic(m.getModifiers()), FileLocation.UNKNOWN);
	}
}
