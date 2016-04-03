package wyvern.target.corewyvernIL.support;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import wyvern.target.corewyvernIL.expression.Value;
import wyvern.target.corewyvernIL.type.ValueType;

/** Resolves abstract module paths to concrete files, then parses the files into modules.
 *  Knows the root directory
 * 
 * @author aldrich
 */
public class ModuleResolver {
	private File rootDir;
	private Map<String, Value> moduleCache = new HashMap<String, Value>();
	
	public ModuleResolver(File rootDir) {
		if (!rootDir.isDirectory())
			throw new RuntimeException("the root path for the module resolver must be a directory");
		this.rootDir = rootDir;
	}
	
	/**
	 * Equivalent to resolveModule, but for types.
	 * Looks for a .wyt instead of a .wyv
	 * 
	 * @param qualifiedName
	 * @return
	 */
	public ValueType resolveType(String qualifiedName) {
		// TODO: implement me
		return null;
	}
	
	/** The main utility function for the ModuleResolver.
	 *  Accepts a string argument of the module name to import
	 *  Returns the uninstantiated module
	 */
	public Value resolveModule(String qualifiedName) {
		Value loadedValue = cacheLookup(qualifiedName);
		if (loadedValue != null)
			return loadedValue;
		File f = resolve(qualifiedName);
		return load(f);
	}
	
	private Value cacheLookup(String qualifiedName) {
		return moduleCache.get(qualifiedName);
	}

	/**
	 * Turns dots into directory slashes.
	 * Adds a .wyv at the end
	 * 
	 * @param qualifiedName
	 * @return
	 */
	private File resolve(String qualifiedName) {
		// TODO: implement me
		return null;
	}
	
	/**
	 * Reads the file.
	 * Parses it, generates IL, and typechecks it.
	 * In the process, loads other modules as necessary.
	 * Stores the resulting value in the cache.
	 * Returns the resulting module.
	 * 
	 * @param file
	 * @return
	 */
	private Value load(File file) {
		// TODO: implement me
		return null;
	}
}
