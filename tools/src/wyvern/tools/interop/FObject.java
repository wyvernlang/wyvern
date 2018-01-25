package wyvern.tools.interop;

import java.util.List;

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
     * Returns null if the called method is void, otherwise an
     * object with the same interpretation as args
     *
     * @param args
     * @return
     * @throws ReflectiveOperationException
     */
    Object invokeMethod(String methodName, List<Object> args) throws ReflectiveOperationException;

    /** Gets the class of the underlying Java object
     */
    Class<?> getJavaClass();

    /** Gets the underlying Java object
     */
    Object getWrappedValue();

    /** Gets hints for the argument types of methodName.
     * Returns null if the method is not found
     */
    Class<?>[] getTypeHints(String methodName);
}
