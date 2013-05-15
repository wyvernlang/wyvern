package wyvern.targets.Java.runtime;

import java.lang.invoke.CallSite;
import java.lang.invoke.ConstantCallSite;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public class Runtime {
	public static CallSite bootstrap(MethodHandles.Lookup caller, String dynMethodName, MethodType dMT) throws NoSuchMethodException, IllegalAccessException {
		System.out.println(dynMethodName);
		Class clazz = caller.lookupClass();
		System.out.println(clazz.getName());
		System.out.println(dMT.parameterArray()[0]);
		
		return new ConstantCallSite(caller.findVirtual(dMT.parameterArray()[0], dynMethodName, MethodType.methodType(dMT.returnType(), dMT.parameterList().subList(1, dMT.parameterCount()))));
	}
}
