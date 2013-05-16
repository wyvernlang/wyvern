package wyvern.targets.Java.runtime;

import java.lang.invoke.*;

public class Runtime {
	public static CallSite bootstrap(MethodHandles.Lookup caller, String dynMethodName, MethodType dMT) throws NoSuchMethodException, IllegalAccessException {
		System.out.println(dynMethodName);
		Class clazz = caller.lookupClass();
		System.out.println(clazz.getName());
		System.out.println(dMT.parameterArray()[0]);
		
		return new MutableCallSite(caller.findVirtual(dMT.parameterArray()[0], dynMethodName, MethodType.methodType(dMT.returnType(), dMT.parameterList().subList(1, dMT.parameterCount()))));
	}
}
