package wyvern.tools.interop;

import wyvern.tools.errors.HasLocation;

/**
 * A generic interface for importing foreign (Java) objects into another language
 */
public interface Importer {
    /**
     * Returns a foreign (Java) object corresponding to the passed-in
     * string.
     *
     * The argument must be of the form:
     *
     * <full package name>.<class name>.<static field>
     *
     * TODO: Other argument forms will likely be supported in the future.
     *
     * @throws ReflectiveOperationException
     */
    FObject find(String qualifiedName, HasLocation errorLocation) throws ReflectiveOperationException;
}
