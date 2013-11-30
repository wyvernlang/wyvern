package wyvern.tools.typedAST.extensions.interop.java.typedAST;

import wyvern.tools.errors.FileLocation;
import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.core.declarations.ClassDeclaration;
import wyvern.tools.typedAST.core.declarations.DeclSequence;
import wyvern.tools.typedAST.extensions.interop.java.types.JavaClassType;
import wyvern.tools.types.Environment;
import wyvern.tools.types.extensions.ClassType;
import wyvern.tools.util.Pair;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

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

            for (Field f : clazz.getFields()) {
                decls.add(new JavaField(f,null,null));//TODO pending upstream push of OpenJDK patch 8009222
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
		updateEnv();
	}

	@Override
	public void updateEnv() {
		AtomicReference<Environment> ref = getTypeEquivalentEnvironmentReference();
		Environment env = ref.get();
		if (env == null)
			env = Environment.getEmptyEnvironment();
		for (Declaration decl : this.getDecls().getDeclIterator()) {
			if (decl instanceof JavaMeth) {
				if (((JavaMeth) decl).isClass())
					continue;
				env = decl.extend(env);
			} else if (decl instanceof JavaField) {
				if (((JavaField) decl).isClass())
					continue;
				env = decl.extend(env);
			} else {
				throw new RuntimeException();
			}
		}
		ref.set(env);
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

    @Override
    public String toString() {
        return "JavaClassDecl("+this.clazz.getName()+")";
    }
}
