package wyvern.tools.typedAST.extensions.interop.java.typedAST;


import wyvern.tools.errors.FileLocation;
import wyvern.tools.typedAST.core.Closure;
import wyvern.tools.typedAST.core.binding.NameBinding;
import wyvern.tools.typedAST.core.binding.NameBindingImpl;
import wyvern.tools.typedAST.core.binding.ValueBinding;
import wyvern.tools.typedAST.core.declarations.DefDeclaration;
import wyvern.tools.typedAST.extensions.interop.java.Util;
import wyvern.tools.typedAST.extensions.interop.java.types.JavaClassType;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.Intersection;
import wyvern.tools.util.Pair;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class JavaMeth extends DefDeclaration {

	private List<JClosure.JavaInvokableMethod> methods = new ArrayList<>();
	static List<String> getNames(Method m) {
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
	//WHY JAVA, WHY
	static List<String> getNames(Constructor m) {
		Class[] args = m.getParameterTypes();
		ArrayList<String> output = new ArrayList<String>();
		for (int i = 0; i < args.length; i++)
			output.add("arg"+i);
		return output;
	}

	private static List<NameBinding> getNameBindings(List<String> paramNames, Class[] parameterTypes) {
		ArrayList<NameBinding> output = new ArrayList<NameBinding>();
		int i = 0;
		for (Class arg : parameterTypes) {
			output.add(new NameBindingImpl(paramNames.get(i++), Util.javaToWyvType(arg)));
		}
		return output;

	}

	public JavaMeth(String name, List<JClosure.JavaInvokableMethod> cstrs) {
		super(name,
				getJMethType(cstrs),
				null,
				null,
				cstrs.get(0).getClassMeth());
		this.methods = cstrs;
	}

	private static Type getJMethType(List<JClosure.JavaInvokableMethod> overloads) {
		List<Type> methTypes = new LinkedList<Type>();
		for (JClosure.JavaInvokableMethod meth : overloads) {
			methTypes.add(
					DefDeclaration
							.getMethodType(getNameBindings(meth.getParamNames(), meth.getParameterTypes()),
									meth.getReturnType()));
		}
		if (methTypes.size() == 1)
			return methTypes.get(0);
		else if (methTypes.size() > 1)
			return new Intersection(methTypes);
		return null;
	}

	@Override
	public void evalDecl(Environment evalEnv, Environment declEnv) {
		JClosure closure = new JClosure(methods, evalEnv);
		ValueBinding vb = (ValueBinding) declEnv.lookup(getName());
		vb.setValue(closure);
	}
	@Override
	public String toString() {
		return "JMeth " + getName();
	}
}
