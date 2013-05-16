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

    private static Object lookup(InliningCacheCallSite callSite, Object[] args) throws Throwable {
        MethodType type = callSite.type();
        if (callSite.depth >= InliningCacheCallSite.MAX_DEPTH) {
            // revert to a vtable call
            MethodHandle target = callSite.lookup.findVirtual(type.parameterType(0), callSite.name,
                    type.dropParameterTypes(0, 1));
            //callSite.setTarget(target);
            return target.invokeWithArguments(args);
        }

        Object receiver = args[0];
        Class<?> receiverClass = receiver.getClass();
        MethodHandle target = callSite.lookup.findVirtual(receiverClass, callSite.name,
                type.dropParameterTypes(0, 1));
        target = target.asType(type);

        //callSite.setTarget(target);
        return target.invokeWithArguments(args);
    }

	public static CallSite bootstrap(MethodHandles.Lookup lookup, String name, MethodType type, Object... args) throws NoSuchMethodException, IllegalAccessException {
        InliningCacheCallSite callSite = new InliningCacheCallSite(lookup, name, type);
        callSite.setTarget(LOOKUP.bindTo(callSite).asCollector(Object[].class, type.parameterCount()).asType(type));
        return callSite;

		//return new ConstantCallSite(caller.findStatic(Runtime.class, "lookup", dMT));
		//return new MutableCallSite(lookup.findVirtual(type.parameterArray()[0], name, MethodType.methodType(type.returnType(), type.parameterList().subList(1, type.parameterCount()))));
	}

    private static MethodHandle LOOKUP;
    static {
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        try {
            LOOKUP = lookup.findStatic(Runtime.class, "lookup",
                    MethodType.methodType(Object.class, InliningCacheCallSite.class, Object[].class));
        } catch (ReflectiveOperationException e) {
            throw (AssertionError)new AssertionError().initCause(e);
        }
    }
}
