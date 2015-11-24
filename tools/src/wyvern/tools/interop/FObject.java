package wyvern.tools.interop;

import java.util.List;

/**
 * Represents a foreign (Java) object in another language
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
}
