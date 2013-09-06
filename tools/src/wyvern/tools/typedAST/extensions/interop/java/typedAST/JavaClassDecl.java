package wyvern.tools.typedAST.extensions.interop.java.typedAST;

import org.mozilla.javascript.edu.emory.mathcs.backport.java.util.Arrays;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.core.declarations.ClassDeclaration;
import wyvern.tools.typedAST.core.declarations.DeclSequence;
import wyvern.tools.typedAST.core.declarations.TypeDeclaration;
import wyvern.tools.typedAST.extensions.interop.java.types.JavaClassType;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.ClassType;
import wyvern.tools.types.extensions.TypeType;
import wyvern.tools.util.Pair;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

public class JavaClassDecl extends ClassDeclaration {
	private Class clazz;

	public Class getClazz() {
		return clazz;
	}

	private static DeclSequence getDecls(Class clazz) {
		List<Declaration> decls = new LinkedList<Declaration>();
		MethodHandles.Lookup lookup = MethodHandles.lookup();

		try {
			List<JClosure.JavaInvokableMethod> cstrs = new LinkedList<>();
			for (Constructor c : clazz.getConstructors()) {
				Class[] parameterTypes = c.getParameterTypes();
				cstrs.add(new JClosure.JavaInvokableMethod(parameterTypes, clazz,
						lookup.unreflectConstructor(c), JavaMeth.getNames(c), true, clazz));
			}
			if (cstrs.size() > 0)
				decls.add(new JavaMeth("new", cstrs));

			HashMap<String, List<Pair<MethodHandle, Method>>> map = new HashMap<>();
			for (Method m : clazz.getMethods()) {
				if (map.containsKey(m.getName())) {
					map.get(m.getName()).add(new Pair<>(lookup.unreflect(findHighestMethod(clazz,m)),m));
					continue;
				}
				ArrayList<Pair<MethodHandle, Method>> list = new ArrayList<>();
				list.add(new Pair<>(lookup.unreflect(findHighestMethod(clazz,m)), m));
				map.put(m.getName(), list);
			}
			for (Map.Entry<String, List<Pair<MethodHandle,Method>>> entry : map.entrySet()) {
				decls.add(new JavaMeth(entry.getKey(), getJavaInvokableMethods(clazz, entry)));
			}
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
		DeclSequence ds = new DeclSequence(decls);
		return ds;
	}

	private static LinkedList<JClosure.JavaInvokableMethod> getJavaInvokableMethods(Class clazz, Map.Entry<String, List<Pair<MethodHandle, Method>>> entry) {
		LinkedList<JClosure.JavaInvokableMethod> overloaded = new LinkedList<>();
		for (Pair<MethodHandle, Method> pair : entry.getValue()) {
			overloaded.add(new JClosure.JavaInvokableMethod(
					pair.second.getParameterTypes(),
					pair.second.getReturnType(),
					pair.first,
					JavaMeth.getNames(pair.second),
					Modifier.isStatic(pair.second.getModifiers()),
					clazz));
		}
		return overloaded;
	}


	public JavaClassDecl(Class clazz) {
		super(clazz.getSimpleName(), "", "", null, FileLocation.UNKNOWN);
		this.clazz = clazz;
	}


	@Override
	public ClassType getClassType() {
		return new JavaClassType(this);
	}

	public void initalize() {
		super.decls = getDecls(this.clazz);
		super.declEnvRef.set(super.decls.extend(Environment.getEmptyEnvironment()));
		super.declEvalEnv = Environment.getEmptyEnvironment();
	}

	private static Method findHighestMethod(Class c, Method m) {
		Class[] ifaces = c.getInterfaces();
		for (int i = 0; i < ifaces.length; i++) {
			Method ifaceMethod = findHighestMethod(ifaces[i], m);
			if (ifaceMethod != null) return ifaceMethod;
		}
		if (c.getSuperclass() != null) {
			Method parentMethod = findHighestMethod(
					c.getSuperclass(), m);
			if (parentMethod != null) return parentMethod;
		}
		try {
			return c.getDeclaredMethod(m.getName(), m.getParameterTypes());
		} catch (NoSuchMethodException e) {
			return null;
		}
	}
}
