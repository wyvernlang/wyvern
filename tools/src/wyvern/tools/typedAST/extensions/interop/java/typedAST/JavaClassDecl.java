package wyvern.tools.typedAST.extensions.interop.java.typedAST;

import wyvern.tools.errors.FileLocation;
import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.core.declarations.ClassDeclaration;
import wyvern.tools.typedAST.core.declarations.DeclSequence;
import wyvern.tools.typedAST.extensions.interop.java.types.JavaClassType;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.ClassType;
import wyvern.tools.types.extensions.Unit;
import wyvern.tools.util.Pair;
import wyvern.tools.util.Reference;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
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
				decls.add(new JavaMeth("create", cstrs));

			//Instance method map
			HashMap<String, List<Pair<MethodHandle, Method>>> map = new HashMap<>();

			//Class method map
			HashMap<String, List<Pair<MethodHandle, Method>>> sMap = new HashMap<>();
			for (Method m : clazz.getMethods()) {
				if (sMap.containsKey(m.getName()) && Modifier.isStatic(m.getModifiers())) {
					sMap.get(m.getName()).add(new Pair<>(lookup.unreflect(findHighestMethod(clazz,m)),m));
					continue;
				} else if (map.containsKey(m.getName())) {
					map.get(m.getName()).add(new Pair<>(lookup.unreflect(findHighestMethod(clazz,m)),m));
					continue;
				}
				ArrayList<Pair<MethodHandle, Method>> list = new ArrayList<>();
				list.add(new Pair<>(lookup.unreflect(findHighestMethod(clazz,m)), m));
				if (!Modifier.isStatic(m.getModifiers()))
					map.put(m.getName(), list);
				else
					sMap.put(m.getName(), list);
			}

            for (Field f : clazz.getFields()) {
                decls.add(new JavaField(f,null,null));//TODO pending upstream push of OpenJDK patch 8009222
            }

			for (Map.Entry<String, List<Pair<MethodHandle,Method>>> entry : sMap.entrySet()) {
				decls.add(new JavaMeth(entry.getKey(), getJavaInvokableMethods(clazz, entry)));
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
	public ClassType getObjType() {
		return new JavaClassType(this);
	}

	@Override
	public Type doTypecheck(Environment env) {
		updateEnv();
		return Unit.getInstance();
	}

	@Override
	public void evalDecl(Environment evalEnv, Environment declEnv) {
		initalize();
		super.evalDecl(evalEnv, declEnv);
	}

	@Override
	public DeclSequence getDecls() {
		initalize();
		return decls;
	}

	boolean initalized = false;
	public void initalize() {
		if (initalized)
			return;
		initalized = true;
		super.decls = getDecls(this.clazz);
		Environment emptyEnvironment = Environment.getEmptyEnvironment();
		super.declEnvRef.set(super.decls.extend(emptyEnvironment, emptyEnvironment));
		super.declEvalEnv = emptyEnvironment;
		updateEnv();
	}

	private boolean envDone = false;
	@Override
	public void updateEnv() {
		if (envDone)
			return;
		envDone = true;
		initalize();
		Environment declEnv = getDeclEnvRef().get();
		Environment objEnv = getObjEnvV();
		if (declEnv == null)
			declEnv = Environment.getEmptyEnvironment();
		if (objEnv == null)
			objEnv = Environment.getEmptyEnvironment();
		for (Declaration decl : this.getDecls().getDeclIterator()) {
			if (decl instanceof JavaMeth) {
				if (((JavaMeth) decl).isClass()) {
					declEnv = decl.extend(declEnv);
					continue;
				}
				objEnv = decl.extend(objEnv);
			} else if (decl instanceof JavaField) {
				if (((JavaField) decl).isClass()) {
					declEnv = decl.extend(declEnv);
					continue;
				}
				objEnv = decl.extend(objEnv);
			} else {
				throw new RuntimeException();
			}
		}
		//getDeclEnvRef().set(declEnv);
		setObjEnv(objEnv);
		envDone = true;
	}

	private static Method findHighestMethod(Class c, Method m) {
		Class[] ifaces = c.getInterfaces();
		for (int i = 0; i < ifaces.length; i++) {
			Method ifaceMethod = findHighestMethod(ifaces[i], m);

			if (ifaceMethod != null) {
				ifaceMethod.setAccessible(true);
				return ifaceMethod;
			}
		}
		if (c.getSuperclass() != null) {
			Method parentMethod = findHighestMethod(
					c.getSuperclass(), m);
			if (parentMethod != null) {
				parentMethod.setAccessible(true);
				return parentMethod;
			}
		}
		try {
			Method declaredMethod = c.getDeclaredMethod(m.getName(), m.getParameterTypes());
			declaredMethod.setAccessible(true);
			return declaredMethod;
		} catch (NoSuchMethodException e) {
			return null;
		}
	}

    @Override
    public String toString() {
        return "JavaClassDecl("+this.clazz.getName()+")";
    }

	@Override
	public Environment evaluateDeclarations(Environment addtlEnv) {
		initalize();
		return super.evaluateDeclarations(addtlEnv);
	}
}
