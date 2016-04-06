package wyvern.target.corewyvernIL.support;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;

import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.expression.Value;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.parsing.coreparser.ParseException;
import wyvern.tools.tests.tagTests.TestUtil;
import wyvern.tools.typedAST.interfaces.ExpressionAST;

/** Resolves abstract module paths to concrete files, then parses the files into modules.
 *  Knows the root directory
 * 
 * @author aldrich
 */
public class ModuleResolver {
	private File rootDir;
	private Map<String, Expression> moduleCache = new HashMap<String, Expression>();
	
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
	 *  Loads a module expression from the file (or looks it up in a cache)
	 *  Returns the uninstantiated module (a function to be applied,
	 *  or an expression to be evaluated)
	 * @throws ParseException 
	 */
	public Expression resolveModule(String qualifiedName) throws ParseException {
		if (!moduleCache.containsKey(qualifiedName)) {
			File f = resolve(qualifiedName);
			moduleCache.put(qualifiedName, load(f));
		}
		return moduleCache.get(qualifiedName);
	}
	
	/**
	 * Turns dots into directory slashes.
	 * Adds a .wyv at the end, and the root to the beginning
	 * 
	 * @param qualifiedName
	 * @return
	 */
	private File resolve(String qualifiedName) {
		String names[] = qualifiedName.split("\\.");
		if (names.length == 0)
			throw new RuntimeException();
		names[names.length - 1] += ".wyv";
		String filename = rootDir.getAbsolutePath();
		for (int i = 0; i < names.length; ++i) {
			filename += File.separatorChar;
			filename += names[i];
		}
		return new File(filename);
	}
	
	/**
	 * Reads the file.
	 * Parses it, generates IL, and typechecks it.
	 * In the process, loads other modules as necessary.
	 * Returns the resulting module expression.
	 * 
	 * @param file
	 * @return
	 */
	private Expression load(File file) throws ParseException {
        String source = TestUtil.readFile(file);
        
        ExpressionAST ast = (ExpressionAST) TestUtil.getNewAST(source);
        
		GenContext genCtx = TestUtil.getStandardGenContext();
        Expression program = ast.generateIL(genCtx, null);
        
		TypeContext ctx = TestUtil.getStandardTypeContext();
        program.typeCheck(ctx);
        
        return program;
	}
}
