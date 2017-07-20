package wyvern.target.corewyvernIL.support;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import wyvern.stdlib.Globals;
import wyvern.target.corewyvernIL.decl.Declaration;
import wyvern.target.corewyvernIL.decl.DefDeclaration;
import wyvern.target.corewyvernIL.decl.ModuleDeclaration;
import wyvern.target.corewyvernIL.decl.NamedDeclaration;
import wyvern.target.corewyvernIL.decl.TypeDeclaration;
import wyvern.target.corewyvernIL.decl.ValDeclaration;
import wyvern.target.corewyvernIL.decltype.DefDeclType;
import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.expression.IExpr;
import wyvern.target.corewyvernIL.expression.Let;
import wyvern.target.corewyvernIL.expression.New;
import wyvern.target.corewyvernIL.expression.Value;
import wyvern.target.corewyvernIL.expression.Variable;
import wyvern.target.corewyvernIL.modules.LoadedType;
import wyvern.target.corewyvernIL.modules.Module;
import wyvern.target.corewyvernIL.modules.TypedModuleSpec;
import wyvern.target.corewyvernIL.type.StructuralType;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.ToolError;
import wyvern.tools.parsing.coreparser.ParseException;
import wyvern.tools.tests.TestUtil;
import wyvern.tools.typedAST.interfaces.ExpressionAST;
import wyvern.tools.typedAST.interfaces.TypedAST;

/** Resolves abstract module paths to concrete files, then parses the files into modules.
 *  Knows the root directory
 * 
 * @author aldrich
 */
public class ModuleResolver {
    private List<File> searchPath;
    private Path platformPath;
    private String platform;
	private Map<String, Module> moduleCache = new HashMap<String, Module>();
	private InterpreterState state;
    private File rootDir;
    private File libDir;
	
    public ModuleResolver(String platform, File rootDir, File libDir) {
    	this.platform = platform;
        this.rootDir = rootDir;
        this.libDir = libDir;
        ArrayList<File> searchPath = new ArrayList<File>();
        if (rootDir != null && !rootDir.isDirectory())
            throw new RuntimeException("the root path \""+rootDir+"\" for the module resolver must be a directory");
        if (libDir != null && !libDir.isDirectory())
            throw new RuntimeException("the lib path \""+libDir+"\" for the module resolver must be a directory");
        if (rootDir != null) {
            searchPath.add(rootDir);
        }
        if (libDir != null) {
            searchPath.add(libDir);
            platformPath = libDir.toPath().resolve("platform").resolve(platform).toAbsolutePath();
			searchPath.add(platformPath.toFile());
        }
        this.searchPath = searchPath;
    }
	
	public void setInterpreterState(InterpreterState s) {
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
		return resolveType(qualifiedName, false);
	}
	
	public LoadedType resolveType(String qualifiedName, boolean toplevel) {
		Module typeDefiningModule;
		if (!moduleCache.containsKey(qualifiedName)) {
			File f = resolve(qualifiedName, true);
			typeDefiningModule = load(qualifiedName, f, toplevel);
			moduleCache.put(qualifiedName, typeDefiningModule);
		} else {
			typeDefiningModule = moduleCache.get(qualifiedName);
		}
		Expression typeDefiningObject = typeDefiningModule.getExpression();
		TypeContext ctx = Globals.getStandardTypeContext();
		final String typeName = typeDefiningObject.typeCheck(ctx).getStructuralType(ctx).getDeclTypes().get(0).getName();
		//final String typeName = ((New)typeDefiningObject).getDecls().get(0).getName();
		//final String generatedVariableName = GenerationEnvironment.generateVariableName();
		return new LoadedType(typeName, typeDefiningModule);
		/*return new ContextBinding(generatedVariableName, typeDefiningObject, typeName) {

			@Override
			public GenContext extendContext(GenContext ctx) {
				return new TypeGenContext(typeName, generatedVariableName, ctx);
			}};*/
	}
	
	public EvalContext contextWith(String... qualifiedNames) {
		EvalContext ctx = Globals.getStandardEvalContext();
		for (String qualifiedName : qualifiedNames) {
			String names[] = qualifiedName.split("\\.");
			String simpleName = names[names.length-1];
			Module module = resolveModule(qualifiedName);
			final Value moduleValue = module.getExpression().interpret(ctx);
			ctx = ctx.extend(simpleName, moduleValue);
			ctx = ctx.extend(module.getSpec().getInternalName(), moduleValue);
		}
		return ctx;
	}
	
	/** The main utility function for the ModuleResolver.
	 *  Accepts a string argument of the module name to import
	 *  Loads a module expression from the file (or looks it up in a cache)
	 *  Returns the uninstantiated module (a function to be applied,
	 *  or an expression to be evaluated)
	 * @throws ParseException 
	 */
	public Module resolveModule(String qualifiedName) {
		return resolveModule(qualifiedName, false);
	}
	
	public Module resolveModule(String qualifiedName, boolean toplevel) {
		if (!moduleCache.containsKey(qualifiedName)) {
			File f = resolve(qualifiedName, false);
			moduleCache.put(qualifiedName, load(qualifiedName, f, toplevel));
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
	public File resolve(String qualifiedName, boolean isType) {
		String names[] = qualifiedName.split("\\.");
		if (names.length == 0)
			throw new RuntimeException();
		names[names.length - 1] += isType?".wyt":".wyv";

    File f = null;
    for (File searchDir : searchPath) {
        f = findFile(names, searchDir.getAbsolutePath());
        if (f.exists())
            break;
    }
		if (f == null || !f.exists()) {
			ToolError.reportError(ErrorMessage.MODULE_NOT_FOUND_ERROR, (FileLocation) null, isType?"type":"module", qualifiedName);
		}
		return f;
	}

	private File findFile(String[] names, String filename) {
		for (int i = 0; i < names.length; ++i) {
			filename += File.separatorChar;
			filename += names[i];
		}
		File f = new File(filename);
		return f;
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
	public Module load(String qualifiedName, File file, boolean toplevel) {
        TypedAST ast = null;
		try {
			ast = TestUtil.getNewAST(file);
		} catch (ParseException e) {
			ToolError.reportError(ErrorMessage.PARSE_ERROR, new FileLocation(file.getPath(), e.currentToken.beginLine, e.currentToken.beginColumn), e.getMessage());
		}
        
		final List<TypedModuleSpec> dependencies = new LinkedList<TypedModuleSpec>();
		GenContext genCtx = Globals.getGenContext(state);
		IExpr program;
		if (ast instanceof ExpressionAST) {
			program = ((ExpressionAST)ast).generateIL(genCtx, null, dependencies);
		} else if (ast instanceof wyvern.tools.typedAST.abs.Declaration) {
			Declaration decl = ((wyvern.tools.typedAST.abs.Declaration) ast).topLevelGen(genCtx, dependencies);
			if (decl instanceof ValDeclaration) {
				program = ((ValDeclaration)decl).getDefinition();
				//program = wrap(program, dependencies);
			} else if (decl instanceof ModuleDeclaration) {
				ModuleDeclaration oldModuleDecl = (ModuleDeclaration) decl;
				ModuleDeclaration moduleDecl = new ModuleDeclaration(Util.APPLY_NAME, oldModuleDecl.getFormalArgs(), oldModuleDecl.getType(), oldModuleDecl.getBody(), oldModuleDecl.getDependencies(), oldModuleDecl.getLocation());
				program = new New(moduleDecl);
			} else if (decl instanceof DefDeclaration) {
				DefDeclaration oldDefDecl = (DefDeclaration) decl;
				// rename according to "apply"
				DefDeclaration defDecl = new DefDeclaration(Util.APPLY_NAME, oldDefDecl.getFormalArgs(), oldDefDecl.getType(), oldDefDecl.getBody(), oldDefDecl.getLocation());
				// wrap in an object
				program = new New(defDecl);
				//program = wrap(program, dependencies);
			} else if (decl instanceof TypeDeclaration) {
				program = new New((NamedDeclaration)decl);
			} else {
				throw new RuntimeException("should not happen");
			}
		} else {
			throw new RuntimeException();
		}
        
		TypeContext ctx = extendContext(Globals.getStandardTypeContext(), dependencies);
        
        return createAdaptedModule(file, qualifiedName, dependencies, program, ctx, toplevel);
	}

	private Module createAdaptedModule(File file, String qualifiedName,
			final List<TypedModuleSpec> dependencies, IExpr program,
			TypeContext ctx, boolean toplevel) {
		
        ValueType moduleType = program.typeCheck(ctx);
		// if this is a platform module, adapt any arguments to take the system.Platform object
		if (file.toPath().toAbsolutePath().startsWith(platformPath)) {
			// if the type is in functor form
			if (moduleType instanceof StructuralType
				&& ((StructuralType)moduleType).getDeclTypes().size()==1
				&& ((StructuralType)moduleType).getDeclTypes().get(0) instanceof DefDeclType
				&& ((StructuralType)moduleType).getDeclTypes().get(0).getName().equals("apply")) {
				DefDeclType appType = (DefDeclType)((StructuralType)moduleType).getDeclTypes().get(0);
				// if the functor takes a system.X object for current platform type X
				ILFactory f = ILFactory.instance();
				ValueType platformType = f.nominalType("system", capitalize(platform));
				ValueType genericPlatformType = f.nominalType("system", "Platform");
				if (appType.getFormalArgs().stream().anyMatch(a -> a.getType().equals(platformType))) {
					// adapt arguments to take the system.Platform object
					List<IExpr> args = appType.getFormalArgs().stream().map(a -> {
						IExpr result = f.variable(a.getName());
						if (a.getType().equals(platformType))
							result = f.cast(result, platformType);
						return result;
					}).collect(Collectors.toList());
					List<ValueType> argTypes = appType.getFormalArgs().stream()
							.map(a -> a.getType().equals(platformType)?genericPlatformType:a.getType())
							.collect(Collectors.toList());
					List<String> argNames = appType.getFormalArgs().stream().map(a -> a.getName()).collect(Collectors.toList());
					IExpr call = f.call(program, "apply", args);
					IExpr fn = f.function("apply", argNames, argTypes, appType.getRawResultType(), call);
					program = fn;
			        moduleType = program.typeCheck(ctx);
				}
			}
		}

    if (!moduleType.isResource(ctx) && !toplevel) {
        Value v = wrap(program, dependencies).interpret(Globals.getStandardEvalContext());
        moduleType = v.getType();
		}

		TypedModuleSpec spec = new TypedModuleSpec(qualifiedName, moduleType);
		return new Module(spec, program, dependencies);
	}

	private String capitalize(String input) {
		return input.substring(0, 1).toUpperCase() + input.substring(1);
	}

	// KEEP THIS CONSISTENT WITH BELOW
	public TypeContext extendContext(TypeContext ctx, List<TypedModuleSpec> dependencies) {
		for (TypedModuleSpec spec : dependencies) {
			final String internalName = spec.getInternalName();
			if (!ctx.isPresent(internalName, true)) {
				ctx = ctx.extend(internalName, spec.getType());
			}
		}
		return ctx;
	}
	
	// KEEP THIS CONSISTENT WITH ABOVE
	public GenContext extendGenContext(GenContext ctx, List<TypedModuleSpec> dependencies) {
		for (TypedModuleSpec spec : dependencies) {
			final String internalName = spec.getInternalName();
			if (!ctx.isPresent(internalName, true)) {
				ctx = ctx.extend(internalName, new Variable(internalName), spec.getType());
			}
		}
		return ctx;
	}
	
	public IExpr wrap(IExpr program, List<TypedModuleSpec> dependencies) {
		for (TypedModuleSpec spec : dependencies) {
			Module m = resolveModule(spec.getQualifiedName());
			program = new Let(m.getSpec().getInternalName(), m.getSpec().getType(), m.getExpression(), program);
		}
		return program;
	}
	
	public static ModuleResolver getLocal() {
		return InterpreterState.getLocalThreadInterpreter().getResolver();
	}

	public String getPlatform() {
		return platform;
	}

    public File getRootDir() {
        return rootDir;
    }

    public File getLibDir() {
        return libDir;
    }
}
