package wyvern.tools.interop;

import java.util.List;

import wyvern.target.corewyvernIL.decl.NamedDeclaration;

/**
 * Represents a foreign (Java) object in another language
 * 
 * This is a different abstraction from JavaValue (in Wyvern) so that this code
 * could one day be reused by another interpreter (for some language other than
 * Wyvern).  All non-Wyvern-specific interop code should go in this package.
 * 
 * @author aldrich
 *
 */
public interface FObject {
	/**
	 * Invokes a method of this object.  The most applicable method
	 * with the appropriate arguments will be invoked.
	 * 
	 * The objects passed in as args can only have types Integer or String (for now)
	 * TODO: Other types will be supported in the near future.
	 * 
	 * Returns null if the called method is void, otherwise an
	 * object with the same interpretation as args
	 * 
	 * @param args
	 * @return
	 * @throws ReflectiveOperationException 
	 */
	Object invokeMethod(String methodName, List<Object> args) throws ReflectiveOperationException;

	Class<?> getJavaClass();

	Object getWrappedValue();
}
