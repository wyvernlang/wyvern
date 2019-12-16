package wyvern.tools;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import wyvern.stdlib.Globals;
import wyvern.target.corewyvernIL.ASTNode;
import wyvern.target.corewyvernIL.astvisitor.PlatformSpecializationVisitor;
import wyvern.target.corewyvernIL.decl.Declaration;
import wyvern.target.corewyvernIL.decl.DefDeclaration;
import wyvern.target.corewyvernIL.decl.ModuleDeclaration;
import wyvern.target.corewyvernIL.decl.NamedDeclaration;
import wyvern.target.corewyvernIL.decl.TypeDeclaration;
import wyvern.target.corewyvernIL.decl.ValDeclaration;
import wyvern.target.corewyvernIL.decltype.DefDeclType;
import wyvern.target.corewyvernIL.expression.IExpr;
import wyvern.target.corewyvernIL.expression.New;
import wyvern.target.corewyvernIL.expression.SeqExpr;
import wyvern.target.corewyvernIL.expression.Value;
import wyvern.target.corewyvernIL.modules.Module;
import wyvern.target.corewyvernIL.modules.TypedModuleSpec;
import wyvern.target.corewyvernIL.support.EvalContext;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.support.ILFactory;
import wyvern.target.corewyvernIL.support.InterpreterState;
import wyvern.target.corewyvernIL.support.ModuleResolver;
import wyvern.target.corewyvernIL.support.TopLevelContext;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.support.Util;
import wyvern.target.corewyvernIL.type.StructuralType;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.parsing.coreparser.ParseException;
import wyvern.tools.tests.TestUtil;
import wyvern.tools.typedAST.core.Script;
import wyvern.tools.typedAST.core.expressions.TaggedInfo;
import wyvern.tools.typedAST.interfaces.ExpressionAST;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.util.Pair;

public class REPL {
    public static final String WYVERN_HOME = System.getenv("WYVERN_HOME");
    public static final String BASE_PATH = WYVERN_HOME == null ? "src/wyvern/tools/tests/"
            : WYVERN_HOME + "/tools/src/wyvern/tools/tests/";
    public static final String STDLIB_PATH = BASE_PATH + "stdlib/";
    public static final String LIB_PATH = WYVERN_HOME == null ? "../stdlib/" : WYVERN_HOME + "/stdlib/";
    public static final String EXAMPLES_PATH = WYVERN_HOME == null ? "../examples/" : WYVERN_HOME + "/examples/";

    private EvalContext programContext;
    private String tempObjCode = "";
    private String tempCode = "";
    private GenContext genContext;
    private String tempModuleType = "";
    private String tempModule = "";
    private ModuleResolver mr;
    private boolean defineModuleType = false;
    private boolean defineModule = false;
    private boolean defineObject = false;
    private String lastInput = "";

    public REPL() {

    }

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        String input = "";
        REPL repl = new REPL();
        System.out.println("Welcome to the Wyvern REPL!");

        while (true) {
            System.out.print("> ");
            System.out.flush();
            input = scanner.nextLine();
            String v = repl.interpretREPL(input);
            if (v != null) {
                System.out.println(v);
            }
        }
    }

    /**
     * Method for interpreting user input from command line to be processed by the
     * REPL
     * 
     * @param the
     *            inputed code from the user.
     * @return The result of the code being interpreted.
     */
    public String interpretREPL(String userInput) {
        lastInput = userInput;
        try {
            if (defineModuleType) {
                if (userInput.equals("") && lastInput.equals("")) {             
                    Value result = updateCode(tempModuleType);
                    tempModuleType = "";
                    defineModuleType = false;
                    return result.toString();
                } else {
                    tempModuleType = tempModuleType + userInput + "\n";
                    //System.out.println(">>");
                }
            } else if (defineModule) {
                if (userInput.equals("") && lastInput.equals("")) {
                    Value result = interpretModule(tempModule);
                    tempModule = "";
                    defineModule = false;
                    return result.toString();
                } else {
                    tempModule = tempModule + userInput + "\n";
                }
            } else if (defineObject) {
                if (userInput.equals("") && lastInput.equals("")) {
                    Value result = updateCode(tempObjCode);
                    tempObjCode = "";
                    defineObject = false;
                    return result.toString();
                } else {
                    tempObjCode = tempObjCode + userInput + "\n";

                }
            } else {
                if (userInput.equals("exit")) {
                    System.exit(1);
                } else if (userInput.equals("genctx")) {
                    return genContext.toString();
                } else if (userInput.equals("evalctx")) {
                    return programContext.toString();
                } else if (userInput.equals("clear")) {
                    tempCode = "";
                } else if (userInput.equals("reset")) {
                    programContext = null;
                    genContext = null;
                    tempCode = "";
                } else if (userInput.equals("code")) {
                    return tempCode;
                } else if (userInput.contains("module def")) {
                    defineModule = true;
                    tempModule = tempModule + userInput + "\n";
                } else if (userInput.contains("resource type")) {
                    defineModuleType = true;
                    tempModuleType = tempModuleType + userInput + "\n";
                } else if (userInput.contains("new")) {
                    defineObject = true;
                    tempObjCode = tempObjCode + userInput + "\n";
                } else {
                    Value v = parse(userInput);
                    if (v != null) {
                        return v.toString();
                    }
                }
            }
        } catch (Exception e) {
            // if error is thrown, code is stored and re-run until a
            // correct line of code in entered to conplete a block, e.g functions
            tempCode = tempCode + userInput + "\n";
            System.out.println("Invalid code, input clear to clear the buffer and start over.\n");
        }
        return null;
    }

    /**
     * Method for processing user input after it has been processed to be process by
     * the interpretREPL method, this method prepares the code to be processed by
     * the Wyvern interpreter
     * 
     * @param The
     *            input that has been processed by interpretREPL
     * @return The result of the code being interpreted from the Wyvern interperter
     */
    public Value parse(String input) throws ParseException {
        String[] lines = input.split("\\r?\\n");
        Value currentResult = null;
        for (String s : lines) {
            if (!(s.length() == 0)) { // does not run code on empty lines
                try {
                    if (tempCode.length() != 0) {
                        currentResult = updateCode(tempCode + s);
                    } else {
                        currentResult = updateCode(s + "\n");
                    }
                } catch (Exception e) {
                    tempCode = tempCode + s + "\n";
                    e.printStackTrace();
                    System.out.println("Invalid code, input clear to clear the buffer and start over.\n");
                }
            }
        }
        return currentResult;
    }

    /**
     * This method runs the code that has been processed through the Wyvern Compiler
     * 
     * @param The
     *            code that will be run
     * @return The result of the code
     */
    public Value updateCode(String input) throws ParseException {
        if (input.length() == 0) {
            // sanity check
            return null;
        }
        // first line of code interpreted before program context and gen context have
        // been created
        if (programContext == null || genContext == null) {
            programContext = Globals.getStandardEvalContext();
            ExpressionAST ast = (ExpressionAST) getNewAST(input, "test input");
            String rootLoc = System.getenv("WYVERN_ROOT");
            if (wyvernRoot.get() != null) {
                rootLoc = wyvernRoot.get();
            } else {
                rootLoc = System.getProperty("user.dir");
            }
            File rootDir = new File(rootLoc);

            GenContext genCtx = Globals.getGenContext(
                    new InterpreterState(InterpreterState.PLATFORM_JAVA, rootDir, new File(LIB_PATH)));

            final LinkedList<TypedModuleSpec> dependencies = new LinkedList<TypedModuleSpec>();

            TopLevelContext tlc = ((Script) ast).generateTLC(genCtx, null, dependencies);

            SeqExpr program = (SeqExpr) tlc.getExpression();
            program = genCtx.getInterpreterState().getResolver().wrap(program, dependencies);
            Pair<Value, EvalContext> result = program.interpretCtx(programContext);

            programContext = result.getSecond();
            genContext = tlc.getContext();
            tempCode = "";
            return result.getFirst();
        } else {
            // program already exists - extend existing context with new code
            ExpressionAST ast = (ExpressionAST) getNewAST(input, "test input");
            final LinkedList<TypedModuleSpec> dependencies = new LinkedList<TypedModuleSpec>();

            TopLevelContext tlc = ((Script) ast).generateTLC(genContext, null, dependencies);

            SeqExpr program = (SeqExpr) tlc.getExpression();
            program = genContext.getInterpreterState().getResolver().wrap(program, dependencies);

            //System.out.println("Interpreting:");

            Pair<Value, EvalContext> result = program.interpretCtx(programContext);

            programContext = result.getSecond();

            genContext = program.extendContext(tlc.getContext());

            tempCode = "";
            return result.getFirst();
        }
    }
    
    //wyvern.tools.typedAST.core.declarations.ModuleDeclaration mdHACK;
    
    /**
     * Method for interpreting modules in REPL
     * 
     * @param the module to interpret
     * 
     * @return The result of the module
     */
    public Value interpretModule(String module) {        
        try {
            String rootLoc = System.getenv("WYVERN_ROOT");
            if (wyvernRoot.get() != null) {
                rootLoc = wyvernRoot.get();
            } else {
                rootLoc = System.getProperty("user.dir");
            }
            File rootDir = new File(rootLoc);
            
            InterpreterState state = new InterpreterState(InterpreterState.PLATFORM_JAVA, rootDir, new File(LIB_PATH));
            state.setGenContext(genContext);
            final LinkedList<TypedModuleSpec> dependencies = new LinkedList<TypedModuleSpec>();
            TypedAST ast = getNewAST(module, "currentCode");
            final Module module1 = resolveModule(ast, state, dependencies);
            
            SeqExpr program = state.getResolver().wrap(module1.getBinding(), module1.getDependencies());
            

            program  = (SeqExpr) PlatformSpecializationVisitor.specializeAST((ASTNode) program, "java", genContext);
            
            Pair<Value, EvalContext> result = program.interpretCtx(programContext); // updates the eval context
            
            programContext = result.getSecond();
            genContext = state.getGenContext().extend(module1.getBindingSite(), module1.getSpec().getType());
            mr = state.getResolver();
            
            return result.getFirst();
        } catch (Exception e) {
            //System.out.println("Except: " + e);
            e.printStackTrace();
            System.out.println("Invalid Module, input clear to clear the buffer and start over.\n");
            return null;
        }
    }
    
    /**
     * Method for resolving an interpreted module
     * 
     * @param TypedAST - the ast that was generated from interpreting the module
     * 
     * @param InterpreterState - state of the program
     * 
     * @param LinkedList<TypedModuleSpec> a list of dependencies.
     * 
     * @return Module created from the inputed params.
     */
    private Module resolveModule(TypedAST ast, InterpreterState state, LinkedList<TypedModuleSpec> dependencies) {
        GenContext genCtx = Globals.getGenContext(state);
        IExpr program;
        String name = "test_expression";
        
        if (ast instanceof ExpressionAST) {
            program = ((ExpressionAST) ast).generateIL(genCtx, null, dependencies);
        } else if (ast instanceof wyvern.tools.typedAST.abs.Declaration) {
            Declaration decl = ((wyvern.tools.typedAST.abs.Declaration) ast).topLevelGen(genCtx, dependencies);
            name = decl.getName();
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
                // rename according to "apply"
                DefDeclaration defDecl = new DefDeclaration(Util.APPLY_NAME, oldDefDecl.getFormalArgs(),
                        oldDefDecl.getType(), oldDefDecl.getBody(), oldDefDecl.getLocation());
                // wrap in an object
                program = new New(defDecl);
                //program = wrap(program, dependencies);
            } else if (decl instanceof TypeDeclaration) {
                program = new New((NamedDeclaration) decl);
            } else {
                throw new RuntimeException("should not happen");
            }
        } else {
            throw new RuntimeException();
        }

        TypeContext ctx = extendContext(genCtx, dependencies); // Globals.getStandardTypeContext()

        return createAdaptedModule(name, dependencies, program, ctx, false, false, state);
        
    }
    
    private Module createAdaptedModule(String qualifiedName,
            final List<TypedModuleSpec> dependencies, IExpr program,
            TypeContext ctx, boolean toplevel, boolean loadingType, InterpreterState state) {
        
        //System.out.println("ctx = " + ctx);

        ValueType moduleType = program.typeCheck(ctx, null);
        // if this is a platform module, adapt any arguments to take the system.Platform object
            // if the type is in functor form
            if (moduleType instanceof StructuralType
                    && ((StructuralType) moduleType).getDeclTypes().size() == 1
                    && ((StructuralType) moduleType).getDeclTypes().get(0) instanceof DefDeclType
                    && ((StructuralType) moduleType).getDeclTypes().get(0).getName().equals("apply")) {
                DefDeclType appType = (DefDeclType) ((StructuralType) moduleType).getDeclTypes().get(0);
                // if the functor takes a system.X object for current platform type X
                ILFactory f = ILFactory.instance();
                ValueType platformType = f.nominalType("system", capitalize(state.getResolver().getPlatform()));
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

        if (!toplevel && !moduleType.isResource(ctx)) {
            Value v = state.getResolver().wrapWithCtx(program, dependencies, Globals.getStandardEvalContext()).interpret(Globals.getStandardEvalContext());
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
        TypedModuleSpec spec = new TypedModuleSpec(qualifiedName, moduleType, typeName, "type name", false);
        return new Module(spec, program, null, dependencies);
    }
    
    public SeqExpr wrapWithCtx(IExpr program, List<TypedModuleSpec> dependencies, EvalContext ctx, InterpreterState state) {
        SeqExpr seqProg = new SeqExpr();
        List<TypedModuleSpec> noDups = state.getResolver().sortDependencies(dependencies);
        for (int i = noDups.size() - 1; i >= 0; --i) {
            TypedModuleSpec spec = noDups.get(i);
            Module m = state.getResolver().resolveModule(spec.getQualifiedName());
            Value v = m.getAsValue(ctx);
            String internalName = m.getSpec().getInternalName();
            ctx = ctx.extend(m.getSpec().getSite(), v);
            ValueType type = m.getSpec().getType();
            seqProg.addBinding(m.getSpec().getSite(), type, v /*m.getExpression()*/, true);
        }
        seqProg.merge(program);
        return seqProg;
    }
    
    public TypeContext extendContext(TypeContext ctx, List<TypedModuleSpec> dependencies) {
        for (TypedModuleSpec spec : dependencies) {
            final String internalName = spec.getInternalName();
            if (!ctx.isPresent(internalName, true)) {
                ctx = ctx.extend(spec.getSite(), spec.getType());
            }
        }
        return ctx;
    }

    /**
     * Method for generating new Abstract syntax tree
     * 
     * @param The
     *            Wyvern code that will be used to create the AST
     * @return The generated AST
     */
    public TypedAST getNewAST(String program, String programName) throws ParseException {
        return TestUtil.getNewAST(program, programName);
    }

    private void clearGlobalTagInfo() {
        TaggedInfo.clearGlobalTaggedInfos();
    }
    
    private String capitalize(String input) {
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }

    // used to set WYVERN_HOME when called programmatically
    private final ThreadLocal<String> wyvernHome = new ThreadLocal<String>();

    // used to set WYVERN_ROOT when called programmatically
    private final ThreadLocal<String> wyvernRoot = new ThreadLocal<String>();
}
