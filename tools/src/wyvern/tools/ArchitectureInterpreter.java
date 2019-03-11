package wyvern.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import wyvern.target.corewyvernIL.FormalArg;
import wyvern.target.corewyvernIL.VarBinding;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.decltype.DefDeclType;
import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.expression.IntegerLiteral;
import wyvern.target.corewyvernIL.expression.Invokable;
import wyvern.target.corewyvernIL.expression.New;
import wyvern.target.corewyvernIL.expression.ObjectValue;
import wyvern.target.corewyvernIL.expression.SeqExpr;
import wyvern.target.corewyvernIL.expression.Value;
import wyvern.target.corewyvernIL.support.EvalContext;
import wyvern.target.corewyvernIL.support.InterpreterState;
import wyvern.target.corewyvernIL.support.Util;
import wyvern.target.corewyvernIL.type.NominalType;
import wyvern.target.corewyvernIL.type.StructuralType;
import wyvern.tools.arch.lexing.ArchLexer;
import wyvern.tools.errors.HasLocation;
import wyvern.tools.errors.ToolError;
import wyvern.tools.interop.JObject;
import wyvern.tools.interop.JavaValue;
import wyvern.tools.interop.JavaWrapper;
import wyvern.tools.parsing.coreparser.ParseException;
import wyvern.tools.parsing.coreparser.WyvernTokenManager;
import wyvern.tools.parsing.coreparser.arch.ASTArchDesc;
import wyvern.tools.parsing.coreparser.arch.ASTComponentDecl;
import wyvern.tools.parsing.coreparser.arch.ASTPortDecl;
import wyvern.tools.parsing.coreparser.arch.ArchParser;
import wyvern.tools.parsing.coreparser.arch.ArchParserConstants;
import wyvern.tools.parsing.coreparser.arch.DeclCheckVisitor;
import wyvern.tools.parsing.coreparser.arch.Node;
import wyvern.tools.tests.TestUtil;

public final class ArchitectureInterpreter {
    protected ArchitectureInterpreter() {
    }

    private static final String CNC_VIEW_FILE_PATH_OPTION = "cnc";
    private static final String DEPLOYMENT_VIEW_FILE_PATH_OPTION = "deploy";
    private static final String PLATFORM_OPTION = "platform";

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        CommandLine cmd = processCommandLineOptions(args);
        String cncFile = cmd.getOptionValue(CNC_VIEW_FILE_PATH_OPTION);
        checkFileReadability(cncFile);
        String deploymentFile = cmd.getOptionValue(DEPLOYMENT_VIEW_FILE_PATH_OPTION);
        if (deploymentFile != null) {
            checkFileReadability(deploymentFile);
        }
        String platform = cmd.getOptionValue(PLATFORM_OPTION);
        if (platform == null) {
            platform = "java";
        } else {
            platform = platform.trim().toLowerCase();
            if (!(platform.equals("java") || platform.equals("python"))) {
                System.err.println("Platform must be java or python");
                System.exit(1);
            }
        }
        try {
            String rootLoc;
            if (wyvernRoot.get() != null) {
                rootLoc = wyvernRoot.get();
            } else {
                rootLoc = System.getProperty("user.dir");
            }
            String wyvernPath = System.getenv("WYVERN_HOME");
            if (wyvernPath == null) {
                if (wyvernHome.get() != null) {
                    wyvernPath = wyvernHome.get();
                } else {
                    System.err.println("must set WYVERN_HOME environmental variable to wyvern project directory");
                    return;
                }
            }
            wyvernPath += "/stdlib/";
            // sanity check: is the wyvernPath a valid directory?
            if (!Files.isDirectory(Paths.get(wyvernPath))) {
                System.err.println("Error: WYVERN_HOME is not set to a valid Wyvern project directory");
                return;
            }
            /*try {
                File f = new File(Paths.get(deploymentFile).toAbsolutePath().toString());
                BufferedReader source = new BufferedReader(new FileReader(f));
                DeploymentViewParser dvp = new DeploymentViewParser(new WyvernTokenManager<>(
                        source, "test", DeploymentViewLexer.class, DeploymentViewParserConstants.class));
                dvp.DeploymentDecl();
            } catch (Exception e) {
                System.out.println(e);
            }*/
/*            // Construct interpreter state
            InterpreterState state = new InterpreterState(InterpreterState.PLATFORM_JAVA,
                                        new File(rootLoc), new File(wyvernPath));

            DeclCheckVisitor visitor = checkArchFile(rootLoc, wyvernPath, filename, filepath, state);

            // Process connectors and begin generation
            HashMap<String, String> connectors = visitor.getConnectors();
            for (String connectorInstance : connectors.keySet()) {
                String connector = connectors.get(connectorInstance);
                HashSet<String> fullports = visitor.getAttachments().get(connectorInstance);
                List<ASTPortDecl> portObjs = new LinkedList<>();
                HashMap portDecls = visitor.getPortDecls();
                for (String p : fullports) {
                    String[] pair = p.split("\\.");
                    portObjs.add((ASTPortDecl) portDecls.get(pair[1]));
                }
                // Load the connector type module and get context
                Module m = state.getResolver().resolveType(connector + "Properties");
                GenContext genCtx = Globals.getGenContext(state);
                EvalContext evalCtx = Globals.getStandardEvalContext();
                SeqExpr mSeqExpr = state.getResolver().wrapWithCtx(m.getExpression(), m.getDependencies(), evalCtx);
                genCtx = mSeqExpr.extendContext(genCtx);
                evalCtx = mSeqExpr.interpretCtx(evalCtx).getSecond();
                // Get AST node
                TypedModuleSpec mSpec = m.getSpec();
                ValueType mType = mSpec.getType();
                StructuralType mSType = mType.getStructuralType(genCtx);
                ConcreteTypeMember connectorDecl = (ConcreteTypeMember) mSType.findDecl(connector + "Properties", genCtx);
                // Interpret type and get metadata
                ConcreteTypeMember contype = (ConcreteTypeMember) connectorDecl.interpret(evalCtx);
                Value metadata = contype.getMetadataValue();
                ValueType metadataType = metadata.getType();
                StructuralType metadataStructure = metadataType.getStructuralType(genCtx);

                // Execute metadata
                String checkPortCompatibility = null, generateConnectorImpl = null, generateConnectorInit = null;
                List<Value> testArgs = new LinkedList<Value>();
                testArgs.add(javaToWyvernList(portObjs));
                for (DeclType dt : metadataStructure.getDeclTypes()) {
                    if (dt instanceof DefDeclType) {
                        DefDeclType defdecl = (DefDeclType) dt;
                        String methodName = defdecl.getName();
                        if (methodName.equals("checkPortCompatibility")) {
                            checkPortCompatibility = methodName;
                        } else if (methodName.equals("generateConnectorImpl")) {
                            generateConnectorImpl = methodName;
                        } else if (methodName.equals("generateConnectorInit")) {
                            generateConnectorInit = methodName;
                        } else {
                            ToolError.reportError(ErrorMessage.INVALID_CONNECTOR_METADATA, FileLocation.UNKNOWN, connector);
                        }
                    }
                }

                if (checkPortCompatibility == null || generateConnectorImpl == null || generateConnectorInit == null) {
                    ToolError.reportError(ErrorMessage.INVALID_CONNECTOR_METADATA, FileLocation.UNKNOWN, connector);
                }

                Value portCompatibility = ((Invokable) metadata).invoke(checkPortCompatibility, testArgs).executeIfThunk();
                if (!((BooleanLiteral) portCompatibility).getValue()) {
                    ToolError.reportError(ErrorMessage.INVALID_CONNECTOR_PORTS, FileLocation.UNKNOWN, connector);
                }
                Value connectorImpl = ((Invokable) metadata).invoke(generateConnectorImpl, testArgs).executeIfThunk();

                int numPortAST = portObjs.size();
                List<Expression> portInstances = unwrapGeneratedAST(numPortAST, connectorImpl, state, evalCtx);
                List<ASTComponentDecl> compOrder = visitor.generateDependencyGraph();

                // generate initAST
                testArgs = new LinkedList<>();
                testArgs.add(javaToWyvernList(portInstances));
                testArgs.add(javaToWyvernList(compOrder));
                Value connectorInit = ((Invokable) metadata).invoke(generateConnectorInit, testArgs).executeIfThunk();
                List<Expression> orderedInitASTs = makeASTOrder(compOrder,
                        unwrapGeneratedAST(numPortAST, connectorInit, state, evalCtx));

                // add init ASTs to contexts
                for (Expression initAST : orderedInitASTs) {
                    if (initAST instanceof SeqExpr) {
                        genCtx = ((SeqExpr) initAST).extendContext(genCtx);
                        evalCtx = ((SeqExpr) initAST).interpretCtx(evalCtx).getSecond();
                    }
                }

                // find and invoke entrypoints
                HashMap<String, String> entrypoints = visitor.getEntrypoints();
                for (String component : entrypoints.keySet()) {
                    String entrypoint = entrypoints.get(component);
                    String initScript = component + "." + entrypoint + "()\n";
                    IExpr program = AST.utils.parseExpression(initScript, genCtx);
                    program.interpret(evalCtx);
                }
                long end = System.currentTimeMillis();
                //System.out.println((end - start) / 1000.0 + "s");
            }*/
        } catch (ToolError/* | FileNotFoundException | ParseException*/ e) {
            e.printStackTrace();
        }
    }

    private static void checkFileReadability(String filename) {
        Path filepath = Paths.get(filename);
        if (!Files.isReadable(filepath)) {
            System.err.println("Cannot read file " + filename);
            System.exit(1);
        }
    }

    private static CommandLine processCommandLineOptions(String[] args) {
        Options cliOptions = new Options();

        cliOptions.addOption(Option.builder(CNC_VIEW_FILE_PATH_OPTION)
                .hasArg()
                .desc("CnC view file path")
                .required()
                .build());

        cliOptions.addOption(Option.builder(DEPLOYMENT_VIEW_FILE_PATH_OPTION)
                .hasArg()
                .desc("Deployment view file path")
                .build());

        cliOptions.addOption(Option.builder(PLATFORM_OPTION)
                .hasArg()
                .desc("Should be either java or python")
                .build());

        CommandLineParser cliParser = new DefaultParser();
        HelpFormatter cliHelpFormatter = new HelpFormatter();
        CommandLine cmd = null;

        try {
            cmd = cliParser.parse(cliOptions, args);
        } catch (org.apache.commons.cli.ParseException e) {
            System.out.println(e.getMessage());
            cliHelpFormatter.printHelp("wyarch", cliOptions);
            System.exit(1);
        }

        return cmd;
    }

    private static List<Expression> makeASTOrder(List<ASTComponentDecl> order, ArrayList<Expression> unordered) {
        List<Expression> orderedExprs = new LinkedList<>();
        for (ASTComponentDecl comp : order) {
            for (int i = 0; i < unordered.size(); i++) {
                Expression seq = unordered.get(i);
                if (seq instanceof SeqExpr) {
                    for (HasLocation elem : ((SeqExpr) seq).getElements()) {
                        if (elem instanceof VarBinding && ((VarBinding) elem).getVarName().contains(comp.getName())) {
                            orderedExprs.add(seq);
                            break;
                        }
                    }
                } else {
                    System.out.println("error?");
                }
            }
        }
        return orderedExprs;
    }

    private static DeclCheckVisitor checkArchFile(String filename, Path filepath, InterpreterState state)
            throws ParseException, FileNotFoundException {
        File f = new File(filepath.toAbsolutePath().toString());
        BufferedReader source = new BufferedReader(new FileReader(f));
        ArchParser wp = new ArchParser(new WyvernTokenManager<>(
                                            source, "test", ArchLexer.class, ArchParserConstants.class));
        wp.fname = filename;
        Node start = wp.ArchDesc();
        DeclCheckVisitor visitor = new DeclCheckVisitor(state);
        visitor.visit((ASTArchDesc) start, null);
        visitor.endOfFileCheck();
        return visitor;
    }

    private static ArrayList<Expression> unwrapGeneratedAST(int numPortAST, Value connectorImpl, InterpreterState state, EvalContext evalCtx) {
        ArrayList<Expression> portInstances = new ArrayList<>();
        Value getFirst = ((Invokable) connectorImpl).invoke("_getFirst", new LinkedList<>()).executeIfThunk();
        // getFirst is an option
        Value value = ((Invokable) getFirst).getField("value");
        // value is an internal.list
        for (int i = 0; i < numPortAST; i++) {
            List<Value> invokeArgs = new LinkedList<>();
            invokeArgs.add(new IntegerLiteral(i));
            Value fromIList = ((Invokable) value).invoke("get", invokeArgs).executeIfThunk();
            // fromIList is another option
            Value option2 = ((Invokable) fromIList).getField("value");
            // a wyvern AST
            JavaValue ast = (JavaValue) ((Invokable) option2).getField("ast");
            // a JavaValue
            JObject obj = (JObject) ast.getFObject();
            Object javaAST = obj.getWrappedValue();
            if (javaAST instanceof New) {
                // module def ASTs from generateConnectorImpl
                Expression newAST = (New) javaAST;
                portInstances.add(newAST);
            } else if (javaAST instanceof SeqExpr) {
                // seqexpr from generateConnectorInit
                SeqExpr seqAST = (SeqExpr) javaAST;
                portInstances.add(seqAST);
            } else if (javaAST instanceof wyvern.tools.typedAST.core.declarations.ModuleDeclaration) {
                state.addModuleAST(((wyvern.tools.typedAST.core.declarations.ModuleDeclaration) javaAST).getName(),
                        (wyvern.tools.typedAST.core.declarations.ModuleDeclaration) javaAST);
            } else {
                throw new RuntimeException("Unexpected expression in generated AST");
            }
        }
        return portInstances;
    }

    private static Value javaToWyvernList(Object result) {
        if (result instanceof List) {
            ObjectValue v = null;
            try {
                v = (ObjectValue) TestUtil
                        .evaluate("import wyvern.collections.list\n" + "list.makeD()\n");
                for (Object elem : (List<?>) result) {
                    List<Value> args = new LinkedList<>();
                    args.add(javaToWyvernList(elem));
                    v.invoke("append", args);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return v;
        } else if (result instanceof ASTPortDecl) {
            List<DeclType> declTypes = new LinkedList<>();
            declTypes.add(new DefDeclType("getRequires", Util.stringType(), new LinkedList<FormalArg>()));
            declTypes.add(new DefDeclType("getProvides", Util.stringType(), new LinkedList<FormalArg>()));
            StructuralType wyvPortDeclType = new StructuralType("PortDecl", declTypes);
            return new JavaValue(JavaWrapper.wrapObject(result), wyvPortDeclType);
        } else if (result instanceof ASTComponentDecl) {
            List<DeclType> declTypes = new LinkedList<>();
            declTypes.add(new DefDeclType("getType", Util.stringType(), new LinkedList<FormalArg>()));
            declTypes.add(new DefDeclType("getName", Util.stringType(), new LinkedList<FormalArg>()));
            StructuralType wyvCompDeclType = new StructuralType("ComponentDecl", declTypes);
            return new JavaValue(JavaWrapper.wrapObject(result), wyvCompDeclType);
        } else if (result instanceof New) {
            return new JavaValue(JavaWrapper.wrapObject(result), new NominalType("ast", "AST"));
        } else {
            // throw error?
            return new JavaValue(JavaWrapper.wrapObject(result), Util.emptyType());
        }
    }

    // used to set WYVERN_HOME when called programmatically
    public static final ThreadLocal<String> wyvernHome = new ThreadLocal<String>();
    // used to set WYVERN_ROOT when called programmatically
    public static final ThreadLocal<String> wyvernRoot = new ThreadLocal<String>();
}
