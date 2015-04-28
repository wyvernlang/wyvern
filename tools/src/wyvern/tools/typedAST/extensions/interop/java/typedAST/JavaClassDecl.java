package wyvern.tools.typedAST.extensions.interop.java.typedAST;

import jdk.internal.dynalink.support.Lookup;
import jdk.nashorn.internal.lookup.MethodHandleFactory;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.core.binding.typechecking.TypeBinding;
import wyvern.tools.typedAST.core.declarations.ClassDeclaration;
import wyvern.tools.typedAST.core.declarations.DeclSequence;
import wyvern.tools.typedAST.core.values.Obj;
import wyvern.tools.typedAST.extensions.interop.java.Util;
import wyvern.tools.typedAST.extensions.interop.java.types.JavaClassType;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.ClassType;
import wyvern.tools.types.extensions.Unit;
import wyvern.tools.util.EvaluationEnvironment;
import wyvern.tools.util.Pair;
import wyvern.tools.util.Reference;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.*;
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
					sMap.get(m.getName()).add(new Pair<>(lookup.unreflect(findHighestMethod(clazz, m)), m));
					continue;
				} else if (map.containsKey(m.getName())) {
					map.get(m.getName()).add(new Pair<>(lookup.unreflect(findHighestMethod(clazz, m)), m));
					continue;
				}
				ArrayList<Pair<MethodHandle, Method>> list = new ArrayList<>();
				list.add(new Pair<>(lookup.unreflect(findHighestMethod(clazz, m)), m));
				if (!Modifier.isStatic(m.getModifiers()))
					map.put(m.getName(), list);
				else
					sMap.put(m.getName(), list);
			}

			//Fields
			for (Field f : clazz.getFields()) {
				Optional<MethodHandle> setter = Optional.empty();
				if (!Modifier.isFinal(f.getModifiers()))
					setter = Optional.of(lookup.unreflectSetter(f));
				decls.add(new JavaField(f, lookup.unreflectGetter(f), setter));
			}

			//Inner classes
			for (Class c : clazz.getDeclaredClasses()) {
				int modifiers = c.getModifiers();
				if (!Modifier.isStatic(modifiers) ||
						Modifier.isPrivate(modifiers) ||
						Modifier.isProtected(modifiers) ||
						Modifier.isAbstract(modifiers))
					continue;
				decls.add(new JavaClassDecl(c));
			}

			//Create real decls
			for (Map.Entry<String, List<Pair<MethodHandle, Method>>> entry : sMap.entrySet()) {
				decls.add(new JavaMeth(entry.getKey(), getJavaInvokableMethods(clazz, entry)));
			}

			for (Map.Entry<String, List<Pair<MethodHandle, Method>>> entry : map.entrySet()) {
				decls.add(new JavaMeth(entry.getKey(), getJavaInvokableMethods(clazz, entry)));
			}


			if (clazz.isArray()) { //add in getter/acessor methods
				Class elementType = clazz.getComponentType();
				MethodHandle gethandle = MethodHandles.arrayElementGetter(clazz);

				String postfix = "";
				switch (elementType.getName()) {
					case "int": postfix = "Int"; break;
					case "boolean": postfix = "Boolean"; break;
					case "byte": postfix = "Byte"; break;
					case "char": postfix = "Char"; break;
					case "double": postfix = "Double"; break;
					case "float": postfix = "Float"; break;
					case "long": postfix = "Long"; break;
					case "short": postfix = "Short"; break;
				}

				MethodHandle sethandle = MethodHandles.arrayElementSetter(clazz);
				MethodHandle lengthHandle = MethodHandles.lookup().unreflect(Class.forName("java.lang.reflect.Array").getMethod("getLength", Object.class));
				decls.add(new JavaMeth("length", Arrays.asList(new JClosure.JavaInvokableMethod(new Class[]{}, int.class, lengthHandle, Arrays.asList(), false, clazz))));
				decls.add(new JavaMeth("get", Arrays.asList(new JClosure.JavaInvokableMethod(new Class[]{int.class}, elementType, gethandle, Arrays.asList("index"), false, clazz))));
				decls.add(new JavaMeth("set", Arrays.asList(new JClosure.JavaInvokableMethod(new Class[]{int.class, elementType}, void.class, sethandle, Arrays.asList("index", "element"), false, clazz))));
			}
		} catch (IllegalAccessException | NoSuchMethodException | ClassNotFoundException e) {
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
		classMembersEnv.setSrc((oSrc) -> () -> {
			initalize();

			//Reset as part of initialization
			return classMembersEnv.get();
		});
		this.clazz = clazz;

		final Optional<Method> creator = Arrays.asList(getClazz().getDeclaredMethods()).stream()
				.filter(meth-> Modifier.isStatic(meth.getModifiers()))
				.filter(meth->meth.getName().equals("meta$get"))
				.filter(meth -> meth.getParameterCount() == 0)
				.findFirst();


		if (creator.isPresent())
				typeBinding = new TypeBinding(typeBinding.getName(), typeBinding.getType(), new Reference<Value>() {
					@Override
					public Value get() {
						try {
							return Util.toWyvObj(creator.get().invoke(null));
						} catch (Exception e) {
							throw new RuntimeException(e);
						}
					}
				});

	}


	@Override
	public ClassType getObjType() {
		return new JavaClassType(this);
	}

	@Override
	public Type doTypecheck(Environment env) {
		updateEnv();
		return new Unit();
	}

	@Override
	public void evalDecl(EvaluationEnvironment evalEnv, EvaluationEnvironment declEnv) {
		initalize();
		super.evalDecl(evalEnv, declEnv);
	}

	@Override
	public DeclSequence getDecls() {
		initalize();
		return decls;
	}

	public Obj getClassObj() {
		initalize();
		return new Obj(getClassEnv(EvaluationEnvironment.EMPTY), null);
	}

	boolean initalized = false;
	public void initalize() {
		if (initalized)
			return;
		initalized = true;
		super.decls = getDecls(this.clazz);
		Environment emptyEnvironment = Environment.getEmptyEnvironment();
		super.classMembersEnv.set(super.decls.extend(emptyEnvironment, emptyEnvironment));
		super.declEvalEnv = EvaluationEnvironment.EMPTY;
		updateEnv();
	}

	private boolean envDone = false;
	@Override
	public void updateEnv() {
		if (envDone)
			return;
		envDone = true;
		initalize();
		Environment declEnv = Environment.getEmptyEnvironment();
		Environment objEnv = getObjEnvV();
		if (declEnv == null)
			declEnv = Environment.getEmptyEnvironment();
		if (objEnv == null)
			objEnv = Environment.getEmptyEnvironment();
		for (Declaration decl : this.getDecls().getDeclIterator()) {
			if (decl instanceof JavaMeth) {
				if (decl.isClassMember()) {
					declEnv = decl.extend(declEnv, declEnv);
					continue;
				}
				objEnv = decl.extend(objEnv, objEnv);
			} else if (decl instanceof JavaField) {
				if (decl.isClassMember()) {
					declEnv = decl.extend(declEnv, declEnv);
					continue;
				}
				objEnv = decl.extend(objEnv, objEnv);
			} else if (decl instanceof JavaClassDecl) {
				declEnv = decl.extend(declEnv, declEnv);
				objEnv = decl.extend(objEnv, declEnv);
			} else {
				throw new RuntimeException();
			}
		}
		getClassMembersEnv().set(declEnv);
		setInstanceMembersEnv(objEnv);
		envDone = true;

		//To generate class env
		extendName(Environment.getEmptyEnvironment(), Environment.getEmptyEnvironment());
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
	public EvaluationEnvironment evaluateDeclarations(EvaluationEnvironment addtlEnv) {
		initalize();
		return super.evaluateDeclarations(addtlEnv);
	}

	@Override
	public Map<String, TypedAST> getChildren() {
		return new HashMap<>();
	}

	@Override
	public TypedAST cloneWithChildren(Map<String, TypedAST> nc) {
		return this;
	}
}
