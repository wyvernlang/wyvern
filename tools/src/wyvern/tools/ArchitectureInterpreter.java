package wyvern.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import wyvern.stdlib.Globals;
import wyvern.target.corewyvernIL.ASTNode;
import wyvern.target.corewyvernIL.FormalArg;
import wyvern.target.corewyvernIL.astvisitor.PlatformSpecializationVisitor;
import wyvern.target.corewyvernIL.astvisitor.TailCallVisitor;
import wyvern.target.corewyvernIL.decl.Declaration;
import wyvern.target.corewyvernIL.decl.ModuleDeclaration;
import wyvern.target.corewyvernIL.decltype.ConcreteTypeMember;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.decltype.DefDeclType;
import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.expression.IExpr;
import wyvern.target.corewyvernIL.expression.IntegerLiteral;
import wyvern.target.corewyvernIL.expression.Invokable;
import wyvern.target.corewyvernIL.expression.JavaValue;
import wyvern.target.corewyvernIL.expression.New;
import wyvern.target.corewyvernIL.expression.ObjectValue;
import wyvern.target.corewyvernIL.expression.SeqExpr;
import wyvern.target.corewyvernIL.expression.Value;
import wyvern.target.corewyvernIL.modules.Module;
import wyvern.target.corewyvernIL.modules.TypedModuleSpec;
import wyvern.target.corewyvernIL.support.EvalContext;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.support.InterpreterState;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.support.Util;
import wyvern.target.corewyvernIL.type.NominalType;
import wyvern.target.corewyvernIL.type.StructuralType;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.arch.lexing.ArchLexer;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.ToolError;
import wyvern.tools.interop.JObject;
import wyvern.tools.interop.JavaWrapper;
import wyvern.tools.parsing.coreparser.ParseException;
import wyvern.tools.parsing.coreparser.TokenManager;
import wyvern.tools.parsing.coreparser.WyvernTokenManager;
import wyvern.tools.parsing.coreparser.arch.ASTArchDesc;
import wyvern.tools.parsing.coreparser.arch.ASTPortDecl;
import wyvern.tools.parsing.coreparser.arch.ArchParser;
import wyvern.tools.parsing.coreparser.arch.ArchParserConstants;
import wyvern.tools.parsing.coreparser.arch.DeclCheckVisitor;
import wyvern.tools.parsing.coreparser.arch.Node;
import wyvern.tools.tests.TestUtil;

public final class ArchitectureInterpreter {
    protected ArchitectureInterpreter() { }
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("usage: wyvarch <filename>");
            System.exit(1);
        }
        String filename = args[0];
        Path filepath = Paths.get(filename);
        if (!Files.isReadable(filepath)) {
            System.err.println("Cannot read file " + filename);
            System.exit(1);
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
                    System.err.println(
                            "must set WYVERN_HOME environmental variable to wyvern project directory");
                    return;
                }
            }
            wyvernPath += "/stdlib/";
            // sanity check: is the wyvernPath a valid directory?
            if (!Files.isDirectory(Paths.get(wyvernPath))) {
                System.err.println(
                        "Error: WYVERN_HOME is not set to a valid Wyvern project directory");
                return;
            }
            // Construct interpreter state
            InterpreterState state = new InterpreterState(
                    InterpreterState.PLATFORM_JAVA, new File(rootLoc),
                    new File(wyvernPath));

            DeclCheckVisitor visitor = checkArchFile(rootLoc, wyvernPath, filename, filepath, state);

            // Process connectors and begin generation
            HashMap<String, String> connectors = visitor.getConnectors();
            for (String connectorInstance : connectors.keySet()) {
                String connector = connectors.get(connectorInstance);
                HashSet<String> fullports = visitor.getAttachments()
                        .get(connectorInstance);
                HashSet<String> ports = new HashSet<>();
                List<ASTPortDecl> portobjs = new LinkedList<>();
                for (String p : fullports) {
                    String[] pair = p.split("\\.");
                    ports.add(pair[1]);
                    portobjs.add(visitor.getPortDecls().get(pair[1]));
                }
                // Load the connector type module and get context
                Module m = state.getResolver().resolveType(connector + "Properties");
                GenContext genCtx = Globals.getGenContext(state);
                EvalContext evalCtx = Globals.getStandardEvalContext();
                SeqExpr mSeqExpr = state.getResolver().wrapWithCtx(m.getExpression(),
                        m.getDependencies(), evalCtx);
                genCtx = mSeqExpr.extendContext(genCtx);
                evalCtx = mSeqExpr.interpretCtx(evalCtx).getSecond();
                // Get AST node
                TypedModuleSpec mSpec = m.getSpec();
                ValueType mType = mSpec.getType();
                StructuralType mSType = mType.getStructuralType(genCtx);
                ConcreteTypeMember connectorDecl = (ConcreteTypeMember) mSType
                        .findDecl(connector + "Properties", genCtx);
                // Interpret type and get metadata
                ConcreteTypeMember contype = (ConcreteTypeMember) connectorDecl
                        .interpret(evalCtx);
                Value metadata = contype.getMetadataValue();
                ValueType metadataType = metadata.getType();
                StructuralType metadataStructure = metadataType
                        .getStructuralType(genCtx);

                // Execute metadata
                Value portCompatibility = null, connectorImpl = null,
                        connectorInit = null;
                for (DeclType dt : metadataStructure.getDeclTypes()) {
                    if (dt instanceof DefDeclType) {
                        DefDeclType defdecl = (DefDeclType) dt;
                        String methodName = defdecl.getName();
                        List<Value> testArgs = new LinkedList<Value>();
                        if (methodName.equals("checkPortCompatibility")) {
                            testArgs.add(javaToWyvernList(portobjs));
                            portCompatibility = ((Invokable) metadata)
                                    .invoke(methodName, testArgs).executeIfThunk();
                        } else if (methodName.equals("generateConnectorImpl")) {
                            testArgs.add(javaToWyvernList(portobjs));
                            connectorImpl = ((Invokable) metadata)
                                    .invoke(methodName, testArgs).executeIfThunk();
                        } else if (methodName.equals("generateConnectorInit")) {
                            // for now
                            connectorInit = portCompatibility;
                        } else {
                            ToolError.reportError(ErrorMessage.INVALID_CONNECTOR_METADATA,
                                    FileLocation.UNKNOWN, connector);
                        }
                    }
                }
                if (portCompatibility == null || connectorImpl == null
                        || connectorInit == null) {
                    ToolError.reportError(ErrorMessage.INVALID_CONNECTOR_METADATA,
                            FileLocation.UNKNOWN, connector);
                }
                int numPortAST = ((IntegerLiteral) portCompatibility).getValue(); // number of ASTs to expect
                List<Expression> portInstances = unwrapGeneratedAST(numPortAST, connectorImpl, state);

                // generate initAST
                List<Value> testArgs = new LinkedList<>();
                testArgs.add(javaToWyvernList(portInstances));
                connectorInit = ((Invokable) metadata)
                        .invoke("generateConnectorInit", testArgs).executeIfThunk();
                JavaValue fromInit = (JavaValue) ((Invokable) connectorInit).getField("ast");
                JObject initASTObj = (JObject) fromInit.getFObject();
                IExpr initAST = (Expression) initASTObj.getWrappedValue();

                // find and call entrypoints
                HashMap<String, String> entrypoints = visitor.getEntrypoints();
                for (String component : entrypoints.keySet()) {
                    String entrypoint = entrypoints.get(component);
                    String initScript = component + "." + entrypoint + "()";
                }
            }
        } catch (ToolError | FileNotFoundException | ParseException e) {
            e.printStackTrace();
        }
        System.out.println("~DONE~");
    }

    public static DeclCheckVisitor checkArchFile(String rootLoc, String wyvernPath, String filename,
                                                 Path filepath, InterpreterState state) throws ParseException, FileNotFoundException {
        File f = new File(rootLoc + "/" + filepath.toString());
        BufferedReader source = new BufferedReader(new FileReader(f));
        ArchParser wp = new ArchParser(
                (TokenManager) new WyvernTokenManager<ArchLexer, ArchParserConstants>(
                        source, "test", ArchLexer.class, ArchParserConstants.class));
        wp.fname = filename;
        Node start = wp.ArchDesc();
        DeclCheckVisitor visitor = new DeclCheckVisitor(state);
        visitor.visit((ASTArchDesc) start, null);
        return visitor;
    }

    public static List<Expression> unwrapGeneratedAST(int numPortAST, Value connectorImpl, InterpreterState state) {
        List<Expression> portInstances = new LinkedList<>();
        Value getFirst = ((Invokable) connectorImpl).invoke("_getFirst", new LinkedList<>()).executeIfThunk();
        // getFirst is an option
        Value value = ((Invokable) getFirst).getField("value");
        // value is an internal.list
        for (int i = 0; i < numPortAST; i++) {
            List<Value> invokeArgs = new LinkedList<>();
            invokeArgs.add(new IntegerLiteral(i));
            Value fromIList = ((Invokable) value).invoke("get", invokeArgs).executeIfThunk();
            // fromIList is another other
            Value option2 = ((Invokable) fromIList).getField("value");
            // a wyvern AST !!!
            JavaValue ast = (JavaValue) ((Invokable) option2).getField("ast");
            // a JavaValue !!!!!!!!!!!!!!!!
            JObject obj = (JObject) ast.getFObject();
            Object javaAST = obj.getWrappedValue();
            if (javaAST instanceof New) {
                New newAST = (New) javaAST;
                String moduleName = null;
                for (Declaration decl : newAST.getDecls()) {
                    if (decl instanceof ModuleDeclaration) {
                        moduleName = ((ModuleDeclaration) decl).getName();
                        state.getResolver().addModuleAST(moduleName, newAST);
                    }
                    portInstances.add(newAST);
                }
            } else {
                System.out.println("error?");
            }
        }
        return portInstances;
    }

    public static Value javaToWyvernList(Object result) {
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