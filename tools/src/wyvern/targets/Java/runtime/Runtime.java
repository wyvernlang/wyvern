package wyvern.targets.Java.runtime;

import java.lang.invoke.*;

public class Runtime {
    static class InliningCacheCallSite extends MutableCallSite {
        private static final int MAX_DEPTH = 3;

        final MethodHandles.Lookup lookup;
        final String name;
        int depth;

        InliningCacheCallSite(MethodHandles.Lookup lookup, String name, MethodType type) {
            super(type);
            this.lookup = lookup;
            this.name = name;
        }
    }
    private static boolean checkClass(Class<?> clazz, Object receiver) {
        return receiver.getClass() == clazz;
    }
    private static Object lookup(InliningCacheCallSite callSite, Object[] args) throws Throwable {
        MethodType type = callSite.type();
        if (callSite.depth >= InliningCacheCallSite.MAX_DEPTH) {
            // revert to a vtable call
            MethodHandle target = callSite.lookup.findVirtual(type.parameterType(0), callSite.name,
                    type.dropParameterTypes(0, 1));
            callSite.setTarget(target);
            return target.invokeWithArguments(args);
        }

        Object receiver = args[0];
        Class<?> receiverClass = receiver.getClass();
        MethodHandle target = callSite.lookup.findVirtual(receiverClass, callSite.name,
                type.dropParameterTypes(0, 1));
        target = target.asType(type);

        MethodHandle test = CHECK_CLASS.bindTo(receiverClass);
        test = test.asType(test.type().changeParameterType(0, type.parameterType(0)));

        MethodHandle guard = MethodHandles.guardWithTest(test, target, callSite.getTarget());
        callSite.depth++;

        callSite.setTarget(guard);
        
        return target.invokeWithArguments(args);
    }
	public static CallSite bootstrap(MethodHandles.Lookup lookup, String name, MethodType type, Object... args) throws NoSuchMethodException, IllegalAccessException {
        InliningCacheCallSite callSite = new InliningCacheCallSite(lookup, name, type);
        callSite.setTarget(LOOKUP.bindTo(callSite).asCollector(Object[].class, type.parameterCount()).asType(type));
        return callSite;

		//return new ConstantCallSite(caller.findStatic(Runtime.class, "lookup", dMT));
		//return new MutableCallSite(lookup.findVirtual(type.parameterArray()[0], name, MethodType.methodType(type.returnType(), type.parameterList().subList(1, type.parameterCount()))));
	}

	private static MethodHandle lookupMethodHandle(InliningCacheCallSite site, Object receiver) throws Throwable {
		MethodType type = site.type();
		Class<?> receiverClass = receiver.getClass();

		MethodHandle target = MethodHandles.dropArguments(site.lookup.findStaticGetter(receiverClass, site.name + "$handle", MethodHandle.class), 0, Object.class);
		MethodHandle test = CHECK_CLASS.bindTo(receiverClass);
		test = test.asType(test.type().changeParameterType(0, type.parameterType(0)));

		MethodHandle guard = MethodHandles.guardWithTest(test, target, site.getTarget());
		site.setTarget(guard);


		MethodHandle methodHandle = (MethodHandle) target.invokeExact(receiver);
		return methodHandle;
	}

	public static CallSite bootstrapMethHandle(MethodHandles.Lookup lookup, String name, MethodType type, Object... args) throws NoSuchMethodException, IllegalAccessException {
		InliningCacheCallSite callSite = new InliningCacheCallSite(lookup, name, type);
		callSite.setTarget(LOOKUP_METHOD_HANDLE.bindTo(callSite).asType(type));
		return callSite;
	}

	public static CallSite bootstrapField(MethodHandles.Lookup lookup, String name, MethodType type, Object... args) throws NoSuchMethodException, IllegalAccessException {
		InliningCacheCallSite callSite = new InliningCacheCallSite(lookup, name, type);
		callSite.setTarget(MethodHandles.insertArguments(LOOKUP_FIELD, 0, callSite).asType(type));
		return callSite;
	}

	private static Object lookupField(InliningCacheCallSite site, Object receiver, Class cls) throws Throwable {
		MethodType type = site.type();
		Class<?> receiverClass = receiver.getClass();
		MethodHandle target = MethodHandles.dropArguments(site.lookup.findGetter(receiverClass, site.name, cls), 1, Class.class);
		target = target.asType(site.type());
		MethodHandle test = CHECK_CLASS.bindTo(receiverClass);
		test = test.asType(test.type().changeParameterType(0, type.parameterType(0)));

		MethodHandle guard = MethodHandles.guardWithTest(test, target, site.getTarget());
		site.setTarget(guard);

		return target.asType(MethodType.methodType(Object.class,Object.class,Class.class)).invokeExact(receiver, cls);
	}

	public static CallSite bootstrapSetField(MethodHandles.Lookup lookup, String name, MethodType type, Object... args) throws NoSuchMethodException, IllegalAccessException {
		InliningCacheCallSite callSite = new InliningCacheCallSite(lookup, name, type);
		callSite.setTarget(MethodHandles.insertArguments(LOOKUP_SET_FIELD, 0, callSite).asType(type));
		return callSite;
	}

	private static void lookupSetField(InliningCacheCallSite site, Object receiver, Object value, Class cls) throws Throwable {
		MethodType type = site.type();
		Class<?> receiverClass = receiver.getClass();
		MethodHandle target = MethodHandles.dropArguments(site.lookup.findSetter(receiverClass, site.name, cls), 2, Class.class);
		target = target.asType(site.type());
		MethodHandle test = CHECK_CLASS.bindTo(receiverClass);
		test = test.asType(test.type().changeParameterType(0, type.parameterType(0)));

		MethodHandle guard = MethodHandles.guardWithTest(test, target, site.getTarget());
		site.setTarget(guard);

		target.asType(MethodType.methodType(void.class,Object.class,Object.class,Class.class)).invokeExact(receiver, value, cls);
	}

    private static final MethodHandle LOOKUP;
    private static final MethodHandle CHECK_CLASS;
	private static final MethodHandle LOOKUP_METHOD_HANDLE;
	private static final MethodHandle LOOKUP_FIELD;
	private static final MethodHandle LOOKUP_SET_FIELD;

	static {
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        try {
            LOOKUP = lookup.findStatic(Runtime.class, "lookup",
                    MethodType.methodType(Object.class, InliningCacheCallSite.class, Object[].class));
            CHECK_CLASS = lookup.findStatic(Runtime.class, "checkClass",
                    MethodType.methodType(boolean.class, Class.class, Object.class));
			LOOKUP_METHOD_HANDLE = lookup.findStatic(Runtime.class, "lookupMethodHandle",
					MethodType.methodType(MethodHandle.class, InliningCacheCallSite.class, Object.class));
			LOOKUP_FIELD = lookup.findStatic(Runtime.class, "lookupField",
					MethodType.methodType(Object.class, InliningCacheCallSite.class, Object.class, Class.class));
			LOOKUP_SET_FIELD = lookup.findStatic(Runtime.class, "lookupSetField",
					MethodType.methodType(void.class, InliningCacheCallSite.class, Object.class, Object.class, Class.class));
        } catch (ReflectiveOperationException e) {
            throw (AssertionError)new AssertionError().initCause(e);
        }
    }
}
