package wyvern.tools.typedAST.extensions.interop.java;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.typedAST.core.expressions.Application;
import wyvern.tools.typedAST.core.expressions.Invocation;
import wyvern.tools.typedAST.core.binding.Binding;
import wyvern.tools.typedAST.core.binding.evaluation.ValueBinding;
import wyvern.tools.typedAST.core.values.*;
import wyvern.tools.typedAST.extensions.interop.java.objects.JavaObj;
import wyvern.tools.typedAST.extensions.interop.java.objects.JavaWyvObject;
import wyvern.tools.typedAST.extensions.interop.java.typedAST.JavaClassDecl;
import wyvern.tools.typedAST.extensions.interop.java.types.JavaClassType;
import wyvern.tools.typedAST.interfaces.ApplyableValue;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.*;
import wyvern.tools.util.EvaluationEnvironment;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicReference;

import static org.objectweb.asm.Opcodes.*;

public class Util {
	private static HashMap<Class, Type> classCache = new HashMap<Class, Type>();
	private static HashMap<Type, Map<Class, Class>> typeCache = new HashMap<>();
	private static HashMap<Class, JavaClassDecl> declCache = new HashMap<>();
	private static WeakHashMap<Object, JavaObj> pregenerated = new WeakHashMap<>();

	private static ByteClassLoader loader = new ByteClassLoader(Util.class.getClassLoader());
	private org.objectweb.asm.Type type;

	private static class ByteClassLoader extends ClassLoader {
		private final Map<String, byte[]> extraClassDefs;

		public ByteClassLoader(ClassLoader parent) {
			super(parent);
			this.extraClassDefs = new HashMap<String, byte[]>();
		}

		public void addClass(String name, byte[] classBytes) {
			extraClassDefs.put(name, classBytes);
		}

		@Override
		protected Class<?> findClass(final String name) throws ClassNotFoundException {
			byte[] classBytes = this.extraClassDefs.remove(name);
			if (classBytes != null) {
				return defineClass(name, classBytes, 0, classBytes.length);
			}
			return super.findClass(name);
		}

	}
	
	/** Converts the Java objects passed as arguments to a tuple of Wyvern objects */
	public static Value toWyvObjs(Object... args) {
		if (args.length == 0) {
			return UnitVal.getInstance(FileLocation.UNKNOWN);
		} else if (args.length == 1) {
			return toWyvObj(args[0]);
		} else {
			Type[] types = new Type[args.length];
			Value[] values = new Value[args.length];
			int idx = 0;
			for (Object o : args) {
				types[idx] = javaToWyvType(o.getClass());
				values[idx++] = toWyvObj(o);
			}
			return new TupleValue(new Tuple(types), values);

		}
	}
	private static JavaClassDecl getDecl(Class toGet) {
		if (declCache.containsKey(toGet))
			return declCache.get(toGet);

		JavaClassDecl decl = new JavaClassDecl(toGet);
		declCache.put(toGet, decl);
		//decl.initalize();
		return decl;
	}
	public static Value toWyvObj(Object arg) {
		if (arg instanceof JavaWyvObject)
			return ((JavaWyvObject) arg).getInnerObj();
		if (arg instanceof String)
			return new StringConstant((String) arg);
		if (arg instanceof Integer)
			return new IntegerConstant((Integer) arg);
		if (arg instanceof Boolean)
			return new BooleanConstant((Boolean) arg);


		return javaToWyvObj(arg);
	}

	public static Value javaToWyvObj(Object arg) {
		JavaClassDecl decl = getDecl(arg.getClass());
		AtomicReference<Value> thisRef = new AtomicReference<>();
		if (pregenerated.containsKey(arg)) {
			return pregenerated.get(arg);
		}
		JavaObj newObj = new JavaObj(decl.getFilledBody(thisRef),arg, decl);
		pregenerated.put(arg, newObj);
		thisRef.set(newObj);
		return newObj;
	}

	private static HashSet<Binding> bindings = new HashSet<>();
	public static void setValueBinding(Object arg, ValueBinding b) {
		if (bindings.contains(b))
			return;
		bindings.add(b);
		Value toSet = toWyvObj(arg);
		b.setValue(toSet);
		bindings.remove(b);
	}

	/** Converts a single Wyvern object to a Java object */
	public static Object toJavaObject(Value arg, Class hint) {
		if (arg instanceof StringConstant)
			return ((StringConstant) arg).getValue();

		if (arg instanceof IntegerConstant)
			return ((IntegerConstant) arg).getValue();

		if (!(arg.getType() instanceof ClassType))
			throw new RuntimeException();

		if (arg instanceof JavaObj) {
			return ((JavaObj) arg).getObj();
		}

		return toJavaClass((Obj)arg, hint);
	}
	public static <T> T toJavaClass(Obj obj, Class<T> cast) {
		if (obj instanceof JavaObj) {
			return (T) ((JavaObj) obj).getObj();
		}

		Class wrapperClass = generateJavaWrapper(obj.getIntEnv(), cast);
		try {
			return (T) wrapperClass.getConstructor(Obj.class).newInstance(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static Type javaToWyvType(Class jType) {
		if (classCache.containsKey(jType))
			return classCache.get(jType);

		Type newType = javaToWyvTypeInternal(jType);
		classCache.put(jType, newType);
		return newType;
	}

	public static JavaClassDecl javaToWyvDecl(Class jClass) {

		JavaClassDecl jcd = getDecl(jClass);
		classCache.put(jClass, jcd.getType()); //Prevent infinite recursion
		//jcd.initalize();
		return jcd;
	}

	public static Type javaToWyvTypeInternal(Class jType) {
		if (jType.equals(int.class))
			return new Int();
		else if (jType.equals(Boolean.class) || jType.equals(boolean.class))
			return new Bool();
		else if (jType.equals(String.class))
			return new Str();
		else if (jType.equals(void.class))
			return new Unit();
		else {
			JavaClassDecl jcd = getDecl(jType);
			classCache.put(jType, jcd.getType()); //Prevent infinite recursion
			//jcd.initalize();
			return jcd.getType();
		}
	}

	public static Class wyvToJavaType(Type type) {
		if (type instanceof Int)
			return int.class;
		if (type instanceof Str)
			return String.class;

		if (type instanceof JavaClassType)
			return ((JavaClassType)type).getInnerClass();

		//if (!(type instanceof ClassType))
		throw new RuntimeException(); //TODO: Think of something cleverer


	}

	private static String getMethodDescriptor(Arrow methType, Method candidate) {
		if (candidate == null) {
			int nArgs = nArgs(methType);
			org.objectweb.asm.Type[] args = new org.objectweb.asm.Type[nArgs];
			org.objectweb.asm.Type objType = org.objectweb.asm.Type.getType(Object.class);
			for (int i = 0; i < nArgs; i++) {
				args[i] = objType;
			}
			return org.objectweb.asm.Type.getMethodDescriptor(objType, args);
		} else {
			return org.objectweb.asm.Type.getMethodDescriptor(candidate);
		}
	}

	private static int nArgs(Arrow methType) {
		int nArgs = 0;
		if (methType.getArgument() instanceof Tuple) {
			nArgs = ((Tuple) methType.getArgument()).getTypeArray().length;
		} else if (methType.getArgument() instanceof Unit) {
			nArgs = 0;
		} else {
			nArgs = 1;
		}
		return nArgs;
	}

	private static Type[] getArgTypes(Arrow methType) {
		Type argType = methType.getArgument();
		if (argType instanceof Tuple)
			return ((Tuple) argType).getTypeArray();
		else if (argType instanceof Unit)
			return new Type[0];
		else
			return new Type[] { argType };
	}

	private static Method findCandidate(String name, Arrow methType, Class javaType) {
		int nArgs = nArgs(methType);
		Type[] args = getArgTypes(methType);

		for (Method m : javaType.getMethods()) {
			if (m.getParameterTypes().length != nArgs)
				continue;
			if (!m.getName().equals(name))
				continue;

			if (!methType.getResult().subtype(javaToWyvType(m.getReturnType())))
				continue;

			int argIdx = 0;
			boolean valid = true;
			for (Class param : m.getParameterTypes()) {
				if (!javaToWyvType(param).subtype(args[argIdx++])) {
					valid = false;
					break;
				}
			}
			if (!valid)
				continue;

			return m;
		}
		return null;
	}

	public static boolean checkCast(Obj ref, Class jClass) {
		Type javaType = javaToWyvType(jClass);
		Type wyvernType = ref.getType();
		return wyvernType.subtype(javaType);
	}



	public static boolean checkTypeCast(Type type, Class arg) {
		Type javaType = javaToWyvType(arg);
		return type.subtype(javaType);
	}

	private static volatile int n = 0; //How many classes have been generated
	private static Class<?> generateJavaWrapper(EvaluationEnvironment toWrap, Class javaType) {
		if (typeCache.containsKey(toWrap)) {
			Map<Class,Class> innerMap = typeCache.get(toWrap);
			if (innerMap.containsKey(javaType)) {
				return innerMap.get(javaType);
			}
		}
		ClassWriter cv = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		String name = "autogen$" + n++ + "$imp$" + javaType.getSimpleName();
		if (!javaType.isInterface())
			cv.visit(V1_7,
					ACC_PUBLIC,
					name,
					null,
					org.objectweb.asm.Type.getType(javaType).getInternalName(),
					new String[] { "wyvern/tools/typedAST/extensions/interop/java/objects/JavaWyvObject" });
		else
			cv.visit(V1_7,
					ACC_PUBLIC,
					name,
					null,
					"java/lang/Object",
					new String[] { org.objectweb.asm.Type.getType(javaType).getInternalName(), "wyvern/tools/typedAST/extensions/interop/java/objects/JavaWyvObject" });

		cv.visitField(ACC_PRIVATE, "objref$wyv", "Lwyvern/tools/typedAST/core/values/Obj;", null, null).visitEnd();
		MethodVisitor mv = cv.visitMethod(ACC_PUBLIC, "<init>", "(Lwyvern/tools/typedAST/core/values/Obj;)V",null,null);
		mv.visitCode();
		mv.visitVarInsn(ALOAD, 0);
		mv.visitInsn(DUP);
		mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V");
		mv.visitVarInsn(ALOAD, 1);
		mv.visitFieldInsn(PUTFIELD, name, "objref$wyv", "Lwyvern/tools/typedAST/core/values/Obj;");
		mv.visitInsn(RETURN);
		mv.visitMaxs(2,1);
		mv.visitEnd();

		mv = cv.visitMethod(ACC_PUBLIC, "getInnerObj", "()Lwyvern/tools/typedAST/core/values/Obj;",null,null);
		mv.visitCode();
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, name, "objref$wyv", "Lwyvern/tools/typedAST/core/values/Obj;");
		mv.visitInsn(ARETURN);
		mv.visitMaxs(2,1);
		mv.visitEnd();
		for (Binding b : toWrap.getBindings()) {
			if (!(b instanceof ValueBinding))
				continue;
			if (!(((ValueBinding) b).getValue(null) instanceof ApplyableValue))
				continue;

			Arrow methType = (Arrow) b.getType();
			String methName = b.getName();
			mv = cv.visitMethod(ACC_PUBLIC,
					methName,
					getMethodDescriptor(methType, findCandidate(methName, methType, javaType)),
					null,
					null);

			Type returnType = methType.getResult();
			Type[] parameterTypes = getArgTypes(methType);

			mv.visitCode();
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, name, "objref$wyv", "Lwyvern/tools/typedAST/core/values/Obj;");
			mv.visitLdcInsn(methName);
			mv.visitLdcInsn(parameterTypes.length);
			mv.visitTypeInsn(ANEWARRAY, org.objectweb.asm.Type.getType(Object.class).getInternalName());
			for (int i = 0; i < parameterTypes.length; i++) {
				mv.visitInsn(DUP);
				mv.visitLdcInsn(i);
				if (parameterTypes[i] instanceof Int) {
					mv.visitVarInsn(ILOAD, i+1);
					mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;");
				} else
					mv.visitVarInsn(ALOAD, i+1);
				mv.visitInsn(AASTORE);
			}
			mv.visitMethodInsn(INVOKESTATIC, "wyvern/tools/typedAST/extensions/interop/java/Util", "doInvoke",
					"(Lwyvern/tools/typedAST/core/values/Obj;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/Object;");
			if (returnType instanceof Int) {
				mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
				mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer","intValue","()I");
				mv.visitInsn(IRETURN);
			} else {
				mv.visitInsn(ARETURN);
			}

			mv.visitMaxs(4, 1 + parameterTypes.length);
			mv.visitEnd();
		}
		cv.visitEnd();
		byte[] bytes = cv.toByteArray();

		loader.addClass(name, bytes);
		try {
			Class<?> aClass = loader.loadClass(name);
			if (typeCache.containsKey(aClass)) {
				typeCache.get(aClass).put(javaType, aClass);
			}
			return aClass;
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Something awful happened!");
		}
	}

	public static Object doInvoke(Obj receiver, String target, Object[] args) {
		Value arguments = toWyvObjs(args);
		return toJavaObject((
				new Application(
						new Invocation(receiver, target, null, FileLocation.UNKNOWN),
						arguments, FileLocation.UNKNOWN)
						.evaluate(EvaluationEnvironment.EMPTY)), null);//Therefore, can only handle strings and ints
	}

	public static Value getInternalValue(Obj receiver, String target) {
		return new Invocation(receiver, target, null, FileLocation.UNKNOWN)
						.evaluate(EvaluationEnvironment.EMPTY);//Therefore, can only handle strings and ints
	}
	

	public static Object doInvokeVarargs(Obj receiver, String target, Object... args) {
		Value arguments = toWyvObjs(args);
		return toJavaObject((
				new Application(
						new Invocation(receiver, target, null, FileLocation.UNKNOWN),
						arguments, FileLocation.UNKNOWN)
						.evaluate(EvaluationEnvironment.EMPTY)), null);//Therefore, can only handle strings and ints
	}

	public static Value invokeValue(Value reciever, String target, Value args) {
		return new Application(
				new Invocation(reciever,target, null, FileLocation.UNKNOWN),
				args, FileLocation.UNKNOWN).evaluate(EvaluationEnvironment.EMPTY);
	}
	public static Value invokeValueVarargs(Value reciever, String target, Value... args) {
		Value iargs;
		if (args.length == 0)
			iargs = UnitVal.getInstance(FileLocation.UNKNOWN);
		else if (args.length == 1)
			iargs = args[0];
		else
			iargs = new TupleValue(null, args);

		return new Application(
				new Invocation(reciever,target, null, FileLocation.UNKNOWN),
				iargs, FileLocation.UNKNOWN).evaluate(EvaluationEnvironment.EMPTY);
	}
}
