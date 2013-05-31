package wyvern.tools.typedAST.extensions.interop.java.typedAST;

import wyvern.tools.errors.FileLocation;
import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.core.declarations.ClassDeclaration;
import wyvern.tools.typedAST.core.declarations.DeclSequence;
import wyvern.tools.types.Environment;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

public class JavaClassDecl extends ClassDeclaration {
	private Class clazz;

	private static DeclSequence getDecls(Class clazz) {
		List<Declaration> decls = new LinkedList<Declaration>();
		MethodHandles.Lookup lookup = MethodHandles.lookup();
		for (Method m : clazz.getMethods()) {
			try {
				decls.add(new JavaMeth(lookup.unreflect(findHighestMethod(clazz, m.getName())), m));
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}
		return new DeclSequence(decls);
	}

	public JavaClassDecl(Class clazz) {
		super(clazz.getName(), null, null, null, FileLocation.UNKNOWN);
		this.clazz = clazz;
	}

	public void initalize() {
		super.decls = getDecls(this.clazz);
		super.declEvalEnv = Environment.getEmptyEnvironment();
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
