package wyvern.target.corewyvernIL.support;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import wyvern.stdlib.Globals;
import wyvern.stdlib.support.backend.BytecodeOuterClass;
import wyvern.target.corewyvernIL.BindingSite;
import wyvern.target.corewyvernIL.astvisitor.TailCallVisitor;
import wyvern.target.corewyvernIL.decl.Declaration;
import wyvern.target.corewyvernIL.decl.DefDeclaration;
import wyvern.target.corewyvernIL.decl.ModuleDeclaration;
import wyvern.target.corewyvernIL.decl.NamedDeclaration;
import wyvern.target.corewyvernIL.decl.TypeDeclaration;
import wyvern.target.corewyvernIL.decl.ValDeclaration;
import wyvern.target.corewyvernIL.decltype.DefDeclType;
import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.expression.IExpr;
import wyvern.target.corewyvernIL.expression.New;
import wyvern.target.corewyvernIL.expression.SeqExpr;
import wyvern.target.corewyvernIL.expression.Value;
import wyvern.target.corewyvernIL.expression.Variable;
import wyvern.target.corewyvernIL.modules.Module;
import wyvern.target.corewyvernIL.modules.TypedModuleSpec;
import wyvern.target.corewyvernIL.type.StructuralType;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.HasLocation;
import wyvern.tools.errors.ToolError;
import wyvern.tools.parsing.coreparser.ParseException;
import wyvern.tools.tests.TestUtil;
import wyvern.tools.typedAST.interfaces.ExpressionAST;
import wyvern.tools.typedAST.interfaces.TypedAST;

/**
 * Resolves abstract module paths to concrete files, then parses the files into modules.
 * Knows the root directory
 *
 * @author aldrich
 */
public class ModuleResolver {
    private List<File> searchPath;
    private List<Path> platformPath;
    private String platform;
    private Map<String, Module> moduleCache = new HashMap<String, Module>();
    private Deque<String> modulesBeingResolved = new ArrayDeque<>();
    private InterpreterState state;
    private File rootDir;
    private File libDir;
    private HashMap<String, wyvern.tools.typedAST.core.declarations.ModuleDeclaration> modules;
    private SeqExpr prelude = null;
    private Module preludeModule = null;

    public ModuleResolver(String platform, File rootDir, File libDir) {
        this.platform = platform;
        this.rootDir = rootDir;
        this.libDir = libDir;
        this.modules = new HashMap<>();
        this.platformPath = new ArrayList<>();

        ArrayList<File> searchPath = new ArrayList<File>();
        if (rootDir != null && !rootDir.isDirectory()) {
            throw new RuntimeException("the root path \"" + rootDir + "\" for the module resolver must be a directory");
        }
        if (libDir != null && !libDir.isDirectory()) {
            throw new RuntimeException("the lib path \"" + libDir + "\" for the module resolver must be a directory");
        }
        if (rootDir != null) {
            searchPath.add(rootDir);
            platformPath.add(rootDir.toPath().resolve("platform").resolve(platform).toAbsolutePath());
        }
        if (libDir != null) {
            searchPath.add(libDir);
            platformPath.add(libDir.toPath().resolve("platform").resolve(platform).toAbsolutePath());
        }
        searchPath.addAll(platformPath.stream().map(path -> path.toFile()).collect(Collectors.toList()));
        this.searchPath = searchPath;
    }

    public void addModuleAST(String moduleName, wyvern.tools.typedAST.core.declarations.ModuleDeclaration moduleAST) {
        if (modules.containsKey(moduleName)) {
            ToolError.reportError(ErrorMessage.DUPLICATE_GENERATED_MODULES, FileLocation.UNKNOWN, moduleName);
        } else {
            modules.put(moduleName, moduleAST);
        }
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
    public Module resolveType(String qualifiedName) {
        return resolveType(qualifiedName, false);
    }

    private Module resolveType(String qualifiedName, boolean toplevel) {
        Module typeDefiningModule;
        if (!moduleCache.containsKey(qualifiedName)) {
            File f = resolve(qualifiedName, true);
            if (f == null || !f.exists()) {
                ToolError.reportError(ErrorMessage.MODULE_NOT_FOUND_ERROR, FileLocation.UNKNOWN, "type", qualifiedName);
            }
            typeDefiningModule = load(qualifiedName, f, toplevel);
            moduleCache.put(qualifiedName, typeDefiningModule);
        } else {
            typeDefiningModule = moduleCache.get(qualifiedName);
        }
        /*Expression typeDefiningObject = typeDefiningModule.getExpression();
        TypeContext ctx = Globals.getStandardTypeContext();
        final String typeName = typeDefiningObject.typeCheck(ctx, null).getStructuralType(ctx).getDeclTypes().get(0).getName();*/
        //final String typeName = typeDefiningModule.getSpec().getDefinedTypeName();
        //final String typeName = ((New)typeDefiningObject).getDecls().get(0).getName();
        //final String generatedVariableName = GenerationEnvironment.generateVariableName();
        //return new LoadedType(typeName, typeDefiningModule);
        return typeDefiningModule;
        /*return new ContextBinding(generatedVariableName, typeDefiningObject, typeName) {
            @Override
            public GenContext extendContext(GenContext ctx) {
                return new TypeGenContext(typeName, generatedVariableName, ctx);
            }};*/
    }

    public EvalContext contextWith(String... qualifiedNames) {
        EvalContext ctx = Globals.getStandardEvalContext();
        for (String qualifiedName : qualifiedNames) {
            String[] names = qualifiedName.split("\\.");
            String simpleName = names[names.length - 1];
            Module module = resolveModule(qualifiedName);
            final Value moduleValue = module.getExpression().interpret(ctx);
            BindingSite simpleBinding = new BindingSite(simpleName);
            ctx = ctx.extend(simpleBinding, moduleValue);
            ctx = ctx.extend(module.getSpec().getSite(), moduleValue);
        }
        return ctx;
    }

    /**
     * The main utility function for the ModuleResolver.
     * Accepts a string argument of the module name to import
     * Loads a module expression from the file (or looks it up in a cache)
     * Returns the uninstantiated module (a function to be applied,
     * or an expression to be evaluated)
     *
     * @throws ParseException
     */
    public Module resolveModule(String qualifiedName) {
        return resolveModule(qualifiedName, false);
    }

    public Module resolveModule(String qualifiedName, boolean toplevel) {
        checkNoCyclicDependencies(qualifiedName);
        if (!moduleCache.containsKey(qualifiedName)) {
            File f = resolve(qualifiedName, false);
            modulesBeingResolved.add(qualifiedName);
            if (f == null || !f.exists()) {
                wyvern.tools.typedAST.core.declarations.ModuleDeclaration moduleAST = modules.get(qualifiedName);
                if (moduleAST == null) {
                    ToolError.reportError(ErrorMessage.MODULE_NOT_FOUND_ERROR, FileLocation.UNKNOWN, "module", qualifiedName);
                } else {
                    Module m = loadContinuation(null, qualifiedName, moduleAST, false, toplevel);
                    moduleCache.put(qualifiedName, m);
                }
            } else {
                moduleCache.put(qualifiedName, load(qualifiedName, f, toplevel));
            }
            modulesBeingResolved.remove(qualifiedName);
        }
        return moduleCache.get(qualifiedName);
    }

    /**
     * Check if trying to resolve the specified module would introduce a cyclic dependency on itself.
     *
     * @param qualifiedName: the name of the module to resolve.
     * @throws ToolError of type ErrorMessage.IMPORT_CYCLE: if there is a cyclic dependency.
     */
    private void checkNoCyclicDependencies(String qualifiedName) {
        if (modulesBeingResolved.contains(qualifiedName)) {
            StringBuilder errorMessage = new StringBuilder(qualifiedName);
            boolean foundQualifiedName = false; // used to ignore things not in the cycle
            while (!modulesBeingResolved.isEmpty()) {
                if (foundQualifiedName) {
                    errorMessage.append(modulesBeingResolved.poll());
                    errorMessage.append(" -> ");
                } else if (modulesBeingResolved.poll().equals(qualifiedName)) {
                    foundQualifiedName = true;
                    errorMessage.append(" -> ");
                }
            }
            errorMessage.append(qualifiedName);
            ToolError.reportError(ErrorMessage.IMPORT_CYCLE, HasLocation.UNKNOWN, errorMessage.toString());
        }
    }

    /**
     * Turns dots into directory slashes.
     * Adds a .wyv at the end, and the root to the beginning
     *
     * @param qualifiedName
     * @return
     */
    public File resolve(String qualifiedName, boolean isType) {
        String[] names = qualifiedName.split("\\.");
        if (names.length == 0) {
            throw new RuntimeException();
        }
        names[names.length - 1] += isType ? ".wyt" : ".wyv";

        File f = findFile2(names);
        if (f == null || !f.exists()) {
            if (!isType) {
                // try to find a type, in case there was no module of the appropriate name
                String lastName = names[names.length - 1];
                names[names.length - 1] = lastName.substring(0, lastName.length() - 4) + ".wyt";
                f = findFile2(names);
            }
        }
        return f;
    }

    private File findFile2(String[] names) {
        File f = null;
        for (File searchDir : searchPath) {
            f = findFile(names, searchDir.getAbsolutePath());
            if (f != null && f.exists()) {
                break;
            }
        }
        return f;
    }

    private File findFile(String[] names, String filename) {
        for (int i = 0; i < names.length; ++i) {
            filename += File.separatorChar;
            filename += names[i];
        }
        File f = new File(filename);
        try {
            File canonical = f.getCanonicalFile();
            String lastName = canonical.getName();
            // make sure capitalization matches on Windows platforms
            if (!lastName.equals(names[names.length - 1])) {
                return null;
            }
        } catch (IOException e) {
            return f;
        }
        return f;
    }

    private Module loadContinuation(File file, String qualifiedName, TypedAST ast, boolean loadingType, boolean toplevel) {
        final List<TypedModuleSpec> dependencies = new LinkedList<TypedModuleSpec>();
        GenContext genCtx = Globals.getGenContext(state);
        IExpr program;
        if (ast instanceof ExpressionAST) {
            program = ((ExpressionAST) ast).generateIL(genCtx, null, dependencies);
        } else if (ast instanceof wyvern.tools.typedAST.abs.Declaration) {
            Declaration decl = ((wyvern.tools.typedAST.abs.Declaration) ast).topLevelGen(genCtx, dependencies);
            if (decl instanceof ValDeclaration) {
                program = ((ValDeclaration) decl).getDefinition();
                //program = wrap(program, dependencies);
            } else if (decl instanceof ModuleDeclaration) {
                ModuleDeclaration oldModuleDecl = (ModuleDeclaration) decl;
                if (oldModuleDecl.getFormalArgs().size() == 0) {
                    program = oldModuleDecl.getBody();
                } else {
                    ModuleDeclaration moduleDecl = new ModuleDeclaration(Util.APPLY_NAME, oldModuleDecl.getFormalArgs(),
                            oldModuleDecl.getType(), oldModuleDecl.getBody(), oldModuleDecl.getDependencies(), oldModuleDecl.getLocation());
                    program = new New(moduleDecl);
                }
            } else if (decl instanceof DefDeclaration) {
                DefDeclaration oldDefDecl = (DefDeclaration) decl;

                // Rename according to "apply"
                DefDeclaration defDecl = new DefDeclaration(
                        Util.APPLY_NAME,
                        oldDefDecl.getFormalArgs(),
                        oldDefDecl.getType(),
                        oldDefDecl.getBody(),
                        oldDefDecl.getLocation()
                );

                // Wrap in an object
                program = new New(defDecl);

                // Perform quantification lifting if possible
                final GenContext newGenCtx = extendGenContext(genCtx, dependencies);
                final New liftResult = QuantificationLifter.liftIfPossible(newGenCtx, program);
                if (liftResult != null) {
                    program = liftResult;
                }
            } else if (decl instanceof TypeDeclaration) {
                program = new New((NamedDeclaration) decl);
            } else {
                throw new RuntimeException("should not happen");
            }
        } else {
            throw new RuntimeException();
        }

        TypeContext ctx = extendContext(Globals.getStandardTypeContext(), dependencies);

        return createAdaptedModule(file, qualifiedName, dependencies, program, ctx, toplevel, loadingType);
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
        boolean loadingType = file.getName().endsWith(".wyt");
        TypedAST ast = null;
        try {
            ast = TestUtil.getNewAST(file);
        } catch (ParseException e) {
            if (e.getCurrentToken() != null) {
                ToolError.reportError(ErrorMessage.PARSE_ERROR,
                        new FileLocation(file.getPath(), e.getCurrentToken().beginLine, e.getCurrentToken().beginColumn), e.getMessage());
            } else {
                ToolError.reportError(ErrorMessage.PARSE_ERROR, FileLocation.UNKNOWN, e.getMessage());
            }
        }
        return loadContinuation(file, qualifiedName, ast, loadingType, toplevel);
    }

    private Module createAdaptedModule(File file, String qualifiedName,
                                       final List<TypedModuleSpec> dependencies, IExpr program,
                                       TypeContext ctx, boolean toplevel, boolean loadingType) {

        ValueType moduleType = program.typeCheck(ctx, null);
        // if this is a platform module, adapt any arguments to take the system.Platform object
        if (file != null && platformPath.stream().anyMatch(path -> file.toPath().toAbsolutePath().startsWith(path))) {
            // if the type is in functor form
            if (moduleType instanceof StructuralType
                    && ((StructuralType) moduleType).getDeclTypes().size() == 1
                    && ((StructuralType) moduleType).getDeclTypes().get(0) instanceof DefDeclType
                    && ((StructuralType) moduleType).getDeclTypes().get(0).getName().equals("apply")) {
                DefDeclType appType = (DefDeclType) ((StructuralType) moduleType).getDeclTypes().get(0);
                // if the functor takes a system.X object for current platform type X
                ILFactory f = ILFactory.instance();
                ValueType platformType = f.nominalType("system", capitalize(platform));
                ValueType genericPlatformType = f.nominalType("system", "Platform");
                if (appType.getFormalArgs().stream().anyMatch(a -> a.getType().equals(platformType))) {
                    // adapt arguments to take the system.Platform object
                    List<IExpr> args = appType.getFormalArgs().stream().map(a -> {
                        IExpr result = f.variable(a.getName());
                        if (a.getType().equals(platformType)) {
                            result = f.cast(result, platformType);
                        }
                        return result;
                    }).collect(Collectors.toList());
                    List<ValueType> argTypes = appType.getFormalArgs().stream()
                            .map(a -> a.getType().equals(platformType) ? genericPlatformType : a.getType())
                            .collect(Collectors.toList());
                    List<String> argNames = appType.getFormalArgs().stream().map(a -> a.getName()).collect(Collectors.toList());
                    IExpr call = f.call(program, "apply", args);
                    IExpr fn = f.function("apply", argNames, argTypes, appType.getRawResultType(), call);
                    program = fn;
                    moduleType = program.typeCheck(ctx, null);
                }
            }
        }

        if (!toplevel && !moduleType.isResource(ctx)) {
            Value v = wrapWithCtx(program, dependencies, Globals.getStandardEvalContext()).interpret(Globals.getStandardEvalContext());
            moduleType = v.getType();
        }

        String typeName = null;
        if (loadingType) {
            typeName = moduleType.getStructuralType(ctx).getDeclTypes().get(0).getName();
        }
        // if not a top-level module, make sure the module type is well-formed
        // top-level modules are exempted from this check because the module returns the thing
        // defined on the last line, and that might not be type-checkable without the things
        // added to the context by previous lines.
        if (!toplevel) {
            moduleType.checkWellFormed(ctx);
        }
        TypedModuleSpec spec = new TypedModuleSpec(qualifiedName, moduleType, typeName);
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
                ctx = ctx.extend(spec.getSite(), spec.getType());
            }
        }
        return ctx;
    }

    // KEEP THIS CONSISTENT WITH ABOVE
    public GenContext extendGenContext(GenContext ctx, List<TypedModuleSpec> dependencies) {
        for (TypedModuleSpec spec : dependencies) {
            final String internalName = spec.getInternalName();
            if (!ctx.isPresent(internalName, true)) {
                ctx = ctx.extend(spec.getSite(), new Variable(spec.getSite()), spec.getType());
            }
        }
        return ctx;
    }

    public SeqExpr addSystemObject(Expression expression) {
        SeqExpr expr = new SeqExpr();
        expr.addBinding(new BindingSite("system"), Globals.getSystemType(), Globals.getSystemValue(), false);
        expr.merge(expression);
        return expr;
    }

    public SeqExpr buildPreludedExpression(Expression expression) {
        Module prelude = Globals.getPreludeModule();
        SeqExpr expr = new SeqExpr();
        expr = addSystemObject(expr);
        expr.merge(prelude.getExpression());
        expr.merge(expression);
        return expr;
    }

    public BytecodeOuterClass.Bytecode emitBytecode(Module module) {
        List<TypedModuleSpec> dependencies = module.getDependencies();
        BytecodeOuterClass.Bytecode.Builder wyb = BytecodeOuterClass.Bytecode.newBuilder();
        wyb.setVersion(BytecodeOuterClass.Bytecode.Version.newBuilder().setMagic(42)
                .setMajor(0).setMinor(1))
                .setPath("");

        BytecodeOuterClass.Module.ValueModule.Builder v = BytecodeOuterClass.Module.ValueModule.newBuilder()
                .setType(module.getSpec().getType().emitBytecodeType())
                .setExpression(buildPreludedExpression(module.getExpression()).emitBytecode());

//        Print CoreWyvernIL for debug
//        try {
//            System.out.println(module.getExpression().prettyPrint());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        wyb.addModules(BytecodeOuterClass.Module.newBuilder().setPath("toplevel").setValueModule(v));

        Module prelude = Globals.getPreludeModule();
        dependencies.addAll(prelude.getDependencies());
        List<TypedModuleSpec> noDups = sortDependencies(dependencies);
        for (TypedModuleSpec spec : noDups) {
            Module dep = resolveModule(spec.getQualifiedName());
            Expression e;
            if (prelude.getDependencies().contains(spec)) {
                e = addSystemObject(dep.getExpression());
            } else {
                e = buildPreludedExpression(dep.getExpression());
            }

            v = BytecodeOuterClass.Module.ValueModule.newBuilder()
                    .setType(dep.getSpec().getType().emitBytecodeType())
                    .setExpression(e.emitBytecode());

            wyb.addModules(BytecodeOuterClass.Module.newBuilder().setPath(spec.getQualifiedName()).setValueModule(v));
        }

        wyb.addAllImports(InterpreterState.getLocalThreadInterpreter().getJavascriptFFIImports());

        return wyb.build();
    }

    /**
     * Wraps this program with all its dependencies.  Unlike wrapWitCtx, we do not cache values as we add dependencies.
     *
     * @param program
     * @param dependencies The modules this program depends on.
     *                     Duplicate modules are OK; duplicates will be eliminated, and
     *                     dependencies will be sorted, before the program is linked.
     * @return
     */
    public SeqExpr wrap(IExpr program, List<TypedModuleSpec> dependencies) {
        final SeqExpr seqProg = new SeqExpr();
        seqProg.merge(program);

        final List<TypedModuleSpec> noDups = sortDependencies(dependencies);

        for (TypedModuleSpec spec : noDups) {
            final Module m = resolveModule(spec.getQualifiedName());
            final Expression liftResult = null; // TODO (@justinlubin)

            final BindingSite bindingSite = m.getSpec().getSite();
            final Expression expression;
            final ValueType valueType;

            if (liftResult != null) {
                expression = liftResult;
                valueType = liftResult.getType();
            } else {
                expression = m.getExpression();
                valueType = m.getSpec().getType();
            }

            seqProg.addBinding(bindingSite, valueType, expression, false);
        }

        return seqProg;
    }

    /**
     * Wraps this program with all its dependencies.  The dependencies are
     * evaluated to values using the ctx, and the values are cached in
     * order to reduce duplicate evaluation.
     *
     * @param program
     * @param dependencies The modules this program depends on.
     *                     Duplicate modules are OK; duplicates will be eliminated, and
     *                     dependencies will be sorted, before the program is linked.
     * @param ctx          The evaluation context to use; should include the prelude if it is needed
     * @return
     */
    public SeqExpr wrapWithCtx(IExpr program, List<TypedModuleSpec> dependencies, EvalContext ctx) {
        SeqExpr seqProg = new SeqExpr();
        List<TypedModuleSpec> noDups = sortDependencies(dependencies);
        for (int i = noDups.size() - 1; i >= 0; --i) {
            TypedModuleSpec spec = noDups.get(i);
            Module m = resolveModule(spec.getQualifiedName());
            Value v = m.getAsValue(ctx);
            ctx = ctx.extend(m.getSpec().getSite(), v);
            ValueType type = m.getSpec().getType();
            seqProg.addBinding(m.getSpec().getSite(), type, v /*m.getExpression()*/, true);
        }
        seqProg.merge(program);
        return seqProg;
    }

    /**
     * Constructs an executable SeqExpr consisting of the following:
     * - The dependencies of the prelude
     * - The prelude itself
     * - The dependencies of the main program (with the prelude's dependencies removed)
     * - The main program
     * <p>
     * It differs from wrap()/wrapWithCtx() mainly in that it does not turn everything into values,
     * which seems to create problems when exporting to Python.
     */
    public SeqExpr wrapForPython(IExpr program, List<TypedModuleSpec> dependencies) {
        SeqExpr seqProg = new SeqExpr();
        List<TypedModuleSpec> deps = new ArrayList<TypedModuleSpec>(dependencies);

        Module prelude = Globals.getPreludeModule();
        addDeps(seqProg, prelude.getDependencies());
        seqProg.merge(prelude.getExpression());
        deps.removeAll(prelude.getDependencies());
        addDeps(seqProg, deps);
        seqProg.merge(program);
        return seqProg;
    }

    /**
     * Does not modify deps.
     * Adds deduplicated deps to seqProg, in order from last to first.
     */
    private void addDeps(SeqExpr seqProg, List<TypedModuleSpec> deps) {
        List<TypedModuleSpec> noDups = sortDependencies(deps);
        for (int i = noDups.size() - 1; i >= 0; --i) {
            TypedModuleSpec spec = noDups.get(i);
            Module m = resolveModule(spec.getQualifiedName());
            ValueType type = m.getSpec().getType();
            seqProg.addBinding(m.getSpec().getSite(), type, m.getExpression(), true);
        }
    }

    /**
     * Returns a fresh list, with duplicates eliminated.
     * The last occurrence of each element is left in the returned list.
     */
    private LinkedList<TypedModuleSpec> deDuplicate(List<TypedModuleSpec> dependencies) {
        Set<String> wrapped = new HashSet<String>();
        LinkedList<TypedModuleSpec> noDups = new LinkedList<TypedModuleSpec>();
        for (int i = dependencies.size() - 1; i >= 0; i--) {
            TypedModuleSpec spec = dependencies.get(i);
            String qualifiedName = spec.getQualifiedName();
            if (!wrapped.contains(qualifiedName)) {
                wrapped.add(qualifiedName);
                noDups.addFirst(spec);
            }
        }
        return noDups;
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

    /**
     * de-duplicates dependencies and sorts them so that if A depends on B, A comes earlier in the list
     */
    public List<TypedModuleSpec> sortDependencies(List<TypedModuleSpec> dependencies) {
        LinkedList<TypedModuleSpec> noDups = deDuplicate(dependencies);
        noDups.sort(new Comparator<TypedModuleSpec>() {

            @Override
            public int compare(TypedModuleSpec o1, TypedModuleSpec o2) {
                Module m1 = resolveModule(o1.getQualifiedName());
                Module m2 = resolveModule(o2.getQualifiedName());
                // if o1 depends on o2 then o1 should come first; wrapping will proceed from the end of the list
                if (m1.dependsOn(o2)) {
                    return -1;
                } else if (m2.dependsOn(o1)) {
                    return 1;
                } else {
                    return 0;
                }
            }

        });
        return noDups;
    }

    public SeqExpr getPreludeIfPresent() {
        return prelude;
    }

    public SeqExpr loadPrelude(File file) {
        preludeModule = ModuleResolver.getLocal().load("<prelude>", file, true);
        prelude = ModuleResolver.getLocal().wrap(preludeModule.getExpression(), preludeModule.getDependencies());
        TailCallVisitor.annotate(prelude);
        prelude.addBinding(new BindingSite("system"), Globals.getSystemType(), Globals.getSystemValue(), false);
        return prelude;
    }

    public Module getPreludeModule() {
        if (prelude == null) {
            Globals.getPrelude();
        }
        return preludeModule;
    }
}
