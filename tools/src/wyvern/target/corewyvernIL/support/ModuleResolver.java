package wyvern.target.corewyvernIL.support;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;

import wyvern.target.corewyvernIL.decl.Declaration;
import wyvern.target.corewyvernIL.decl.ValDeclaration;
import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.expression.Value;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.ToolError;
import wyvern.tools.parsing.coreparser.ParseException;
import wyvern.tools.tests.tagTests.TestUtil;
import wyvern.tools.typedAST.interfaces.ExpressionAST;
import wyvern.tools.typedAST.interfaces.TypedAST;

/** Resolves abstract module paths to concrete files, then parses the files into modules.
 *  Knows the root directory
 * 
 * @author aldrich
 */
public class ModuleResolver {
	private File rootDir;
	private Map<String, Expression> moduleCache = new HashMap<String, Expression>();
	private InterpreterState state;
	
	public ModuleResolver(File rootDir) {
		if (!rootDir.isDirectory())
			throw new RuntimeException("the root path for the module resolver must be a directory");
		this.rootDir = rootDir;
	}
	
	void setInterpreterState(InterpreterState s) {
		state = s;
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
	public Expression resolveModule(String qualifiedName) {
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
	 * @param state 
	 * @return
	 */
	private Expression load(File file) {
        String source = TestUtil.readFile(file);
        
        TypedAST ast = null;
		try {
			ast = TestUtil.getNewAST(source);
		} catch (ParseException e) {
			ToolError.reportError(ErrorMessage.PARSE_ERROR, new FileLocation(source, e.currentToken.beginLine, e.currentToken.beginColumn), e.getMessage());
		}
        
		GenContext genCtx = TestUtil.getGenContext(state);
		Expression program;
		if (ast instanceof ExpressionAST) {
			program = ((ExpressionAST)ast).generateIL(genCtx, null);
		} else if (ast instanceof wyvern.tools.typedAST.abs.Declaration) {
			Declaration decl = ((wyvern.tools.typedAST.abs.Declaration) ast).topLevelGen(genCtx);
			if (decl instanceof ValDeclaration) {
				program = ((ValDeclaration)decl).getDefinition();
			} else {
				throw new RuntimeException();
			}
		} else {
			throw new RuntimeException();
		}
        
		TypeContext ctx = TestUtil.getStandardTypeContext();
        program.typeCheck(ctx);
        
        return program;
	}
}
