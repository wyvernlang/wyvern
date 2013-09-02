package wyvern.tools.typedAST.extensions.interop.java.typedAST;

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

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

public class JavaClassDecl extends ClassDeclaration {
	private Class clazz;

	public Class getClazz() {
		return clazz;
	}

	private static DeclSequence getDecls(Class clazz) {
		List<Declaration> decls = new LinkedList<Declaration>();
		MethodHandles.Lookup lookup = MethodHandles.lookup();

		try {
			for (Constructor c : clazz.getConstructors()) {
				Class[] parameterTypes = c.getParameterTypes();
				String newName = "new" + getTypeAppendation(parameterTypes);
				decls.add(new JavaMeth(newName, clazz, lookup.unreflectConstructor(c), c));
			}
			for (Method m : clazz.getMethods()) {
				decls.add(new JavaMeth(m.getName() + getTypeAppendation(m.getParameterTypes()),
						lookup.unreflect(findHighestMethod(clazz, m.getName())), m));
			}
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
		DeclSequence ds = new DeclSequence(decls);
		return ds;
	}

	private static String getTypeAppendation(Class[] parameterTypes) {
		StringBuilder argNameAddons = new StringBuilder(parameterTypes.length);
		for (Class arg : parameterTypes)
			argNameAddons.append(arg.getSimpleName().substring(0,1));
		return argNameAddons.toString();
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

	public TypeDeclaration getEquivType() {
		return new TypeDeclaration(getName(), getDecls(), getLocation());
	}

	private static Method findHighestMethod(Class cls,
											String method) {
		Class[] ifaces = cls.getInterfaces();
		for (int i = 0; i < ifaces.length; i++) {
			Method ifaceMethod = findHighestMethod(ifaces[i], method);
			if (ifaceMethod != null) return ifaceMethod;
		}
		if (cls.getSuperclass() != null) {
			Method parentMethod = findHighestMethod(
					cls.getSuperclass(), method);
			if (parentMethod != null) return parentMethod;
		}
		Method[] methods  = cls.getMethods();
		for (int i = 0; i < methods.length; i++) {
			// we ignore parameter types for now - you need to add this
			if (methods[i].getName().equals(method)) {
				return methods[i];
			}
		}
		return null;
	}
}
