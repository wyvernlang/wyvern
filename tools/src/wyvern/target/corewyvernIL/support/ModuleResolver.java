package wyvern.target.corewyvernIL.support;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.Assert;

import wyvern.target.corewyvernIL.ContextBinding;
import wyvern.target.corewyvernIL.decl.Declaration;
import wyvern.target.corewyvernIL.decl.DefDeclaration;
import wyvern.target.corewyvernIL.decl.NamedDeclaration;
import wyvern.target.corewyvernIL.decl.TypeDeclaration;
import wyvern.target.corewyvernIL.decl.ValDeclaration;
import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.expression.Let;
import wyvern.target.corewyvernIL.expression.New;
import wyvern.target.corewyvernIL.expression.Value;
import wyvern.target.corewyvernIL.modules.LoadedType;
import wyvern.target.corewyvernIL.modules.Module;
import wyvern.target.corewyvernIL.modules.TypedModuleSpec;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.ToolError;
import wyvern.tools.parsing.coreparser.ParseException;
import wyvern.tools.tests.tagTests.TestUtil;
import wyvern.tools.typedAST.interfaces.ExpressionAST;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.transformers.GenerationEnvironment;

/** Resolves abstract module paths to concrete files, then parses the files into modules.
 *  Knows the root directory
 * 
 * @author aldrich
 */
public class ModuleResolver {
	private File rootDir;
	private Map<String, Module> moduleCache = new HashMap<String, Module>();
	private InterpreterState state;
	
	public ModuleResolver(File rootDir) {
		if (rootDir != null && !rootDir.isDirectory())
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
	public LoadedType resolveType(String qualifiedName) {
		Module typeDefiningModule;
		if (!moduleCache.containsKey(qualifiedName)) {
			File f = resolve(qualifiedName, true);
			typeDefiningModule = load(qualifiedName,f);
			moduleCache.put(qualifiedName, typeDefiningModule);
		} else {
			typeDefiningModule = moduleCache.get(qualifiedName);
		}
		Expression typeDefiningObject = typeDefiningModule.getExpression();
		final String typeName = ((New)typeDefiningObject).getDecls().get(0).getName();
		//final String generatedVariableName = GenerationEnvironment.generateVariableName();
		return new LoadedType(typeName, typeDefiningModule);
		/*return new ContextBinding(generatedVariableName, typeDefiningObject, typeName) {

			@Override
			public GenContext extendContext(GenContext ctx) {
				return new TypeGenContext(typeName, generatedVariableName, ctx);
			}};*/
	}
	
	/** The main utility function for the ModuleResolver.
	 *  Accepts a string argument of the module name to import
	 *  Loads a module expression from the file (or looks it up in a cache)
	 *  Returns the uninstantiated module (a function to be applied,
	 *  or an expression to be evaluated)
	 * @throws ParseException 
	 */
	public Module resolveModule(String qualifiedName) {
		if (!moduleCache.containsKey(qualifiedName)) {
			File f = resolve(qualifiedName, false);
			moduleCache.put(qualifiedName, load(qualifiedName, f));
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
	private File resolve(String qualifiedName, boolean isType) {
		String names[] = qualifiedName.split("\\.");
		if (names.length == 0)
			throw new RuntimeException();
		names[names.length - 1] += isType?".wyt":".wyv";
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
	private Module load(String qualifiedName, File file) {
        String source = TestUtil.readFile(file);
        
        TypedAST ast = null;
		try {
			ast = TestUtil.getNewAST(source);
		} catch (ParseException e) {
			e.printStackTrace();
			ToolError.reportError(ErrorMessage.PARSE_ERROR, new FileLocation(file.getPath(), e.currentToken.beginLine, e.currentToken.beginColumn), e.getMessage());
		}
        
		final List<TypedModuleSpec> dependencies = new LinkedList<TypedModuleSpec>();
		GenContext genCtx = TestUtil.getGenContext(state);
		Expression program;
		if (ast instanceof ExpressionAST) {
			program = ((ExpressionAST)ast).generateIL(genCtx, null);
		} else if (ast instanceof wyvern.tools.typedAST.abs.Declaration) {
			Declaration decl = ((wyvern.tools.typedAST.abs.Declaration) ast).topLevelGen(genCtx, dependencies);
			if (decl instanceof ValDeclaration) {
				program = ((ValDeclaration)decl).getDefinition();
			} else if (decl instanceof DefDeclaration) {
				DefDeclaration oldDefDecl = (DefDeclaration) decl;
				// rename according to "apply"
				DefDeclaration defDecl = new DefDeclaration(Util.APPLY_NAME, oldDefDecl.getFormalArgs(), oldDefDecl.getType(), oldDefDecl.getBody(), oldDefDecl.getLocation());
				// wrap in an object
				program = new New(defDecl);
				program = wrap(program, dependencies);
			} else if (decl instanceof TypeDeclaration) {
				program = new New((NamedDeclaration)decl);
			} else {
				throw new RuntimeException("should not happen");
			}
		} else {
			throw new RuntimeException();
		}
        
		TypeContext ctx = TestUtil.getStandardTypeContext();
        ValueType moduleType = program.typeCheck(ctx);
        
        TypedModuleSpec spec = new TypedModuleSpec(qualifiedName, moduleType);
		return new Module(spec, program, dependencies);
	}

	public Expression wrap(Expression program, List<TypedModuleSpec> dependencies) {
		for (TypedModuleSpec spec : dependencies) {
			Module m = resolveModule(spec.getQualifiedName());
			program = new Let(m.getSpec().getQualifiedName(), m.getExpression(), program);
		}
		return program;
	}
}
