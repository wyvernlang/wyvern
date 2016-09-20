package wyvern.tools.tests;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.Optional;

import org.junit.Assert;

import edu.umn.cs.melt.copper.runtime.logging.CopperParserException;
import wyvern.stdlib.Globals;
import wyvern.target.corewyvernIL.decl.DefDeclaration;
import wyvern.target.corewyvernIL.expression.FieldGet;
import wyvern.target.corewyvernIL.expression.IExpr;
import wyvern.target.corewyvernIL.expression.IntegerLiteral;
import wyvern.target.corewyvernIL.expression.Value;
import wyvern.target.corewyvernIL.modules.Module;
import wyvern.target.corewyvernIL.modules.TypedModuleSpec;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.support.InterpreterState;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.support.Util;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.ToolError;
import wyvern.tools.imports.extensions.WyvernResolver;
import wyvern.tools.parsing.coreparser.ParseException;
import wyvern.tools.parsing.coreparser.ParseUtils;
import wyvern.tools.parsing.coreparser.Token;
import wyvern.tools.parsing.coreparser.WyvernParser;
import wyvern.tools.parsing.coreparser.WyvernParserConstants;
import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.core.expressions.TaggedInfo;
import wyvern.tools.typedAST.interfaces.ExpressionAST;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.types.Type;

public class TestUtil {
    public static final String BASE_PATH = "src/wyvern/tools/tests/";
    public static final String STDLIB_PATH = BASE_PATH + "stdlib/";
    public static final String LIB_PATH = "../stdlib/";
    private static final String PLATFORM_PATH = BASE_PATH + "platform/java/stdlib/";
	public static final String PATH = BASE_PATH + "modules/module/";
    
    /** Sets up the standard library and platform paths in the Wyvern resolver
     * 
     */
    public static void setPaths() {
        WyvernResolver.getInstance().resetPaths();
        WyvernResolver.getInstance().addPath(STDLIB_PATH);
        WyvernResolver.getInstance().addPath(PLATFORM_PATH);
    }

    /**
     * Converts the given program into the AST representation.
     * 
     * @param program
     * @return
     * @throws IOException 
     * @throws CopperParserException 
     */
    @Deprecated
    public static TypedAST getAST(String program) throws CopperParserException, IOException {
        return null;
    }
    
    /**
     * Converts the given program into the TypedAST representation, using the
     * new Wyvern parser.
     * 
     * @param program
     * @param programName TODO
     * @return
     * @throws IOException 
     * @throws CopperParserException 
     */
    public static TypedAST getNewAST(String program, String programName) throws ParseException {
        clearGlobalTagInfo();
        Reader r = new StringReader(program);
        WyvernParser<TypedAST,Type> wp = ParseUtils.makeParser(programName, r);
        TypedAST result = wp.CompilationUnit();
        final Token nextToken = wp.token_source.getNextToken();
        if (nextToken.kind != WyvernParserConstants.EOF) {
            ToolError.reportError(ErrorMessage.UNEXPECTED_INPUT, wp.loc(nextToken));            
        }
        return result;
    }
    
    /**
     * Loads and parses the given file into the TypedAST representation, using the
     * new Wyvern parser.
     * 
     * @param program
     * @return
     * @throws IOException 
     * @throws CopperParserException 
     */
    public static TypedAST getNewAST(File programLocation) throws ParseException {
        String program = readFile(programLocation);
        return getNewAST(program, programLocation.getPath());
    }
    
    /**
     * Completely evaluates the given AST, and compares it to the given value.
     * Does typechecking first, then evaluation.
     * 
     * @param ast
     * @param value
     */
    @Deprecated
    public static void evaluateExpecting(TypedAST ast, int value) {
        ast.typecheck(Globals.getStandardEnv(), Optional.empty());
        wyvern.tools.typedAST.interfaces.Value v = ast.evaluate(Globals.getStandardEvalEnv());
        String expecting = "IntegerConstant(" + value + ")";
        Assert.assertEquals(expecting, v.toString());
    }
    
    @Deprecated
    public static void evaluateExpecting(TypedAST ast, String value) {
        ast.typecheck(Globals.getStandardEnv(), Optional.empty());
        wyvern.tools.typedAST.interfaces.Value v = ast.evaluate(Globals.getStandardEvalEnv());
        String expecting = "StringConstant(\"" + value + "\")"; 
        Assert.assertEquals(expecting, v.toString());
    }

    /**
     * Completely evaluates the given AST, and compares it to the given value.
     * Does typechecking first, then evaluation.
     * 
     * @param ast
     * @param value
     */
    @Deprecated
    public static void evaluateExpectingPerf(TypedAST ast, int value) {
        wyvern.tools.typedAST.interfaces.Value v = ast.evaluate(Globals.getStandardEvalEnv());
        String expecting = "IntegerConstant(" + value + ")"; 
        Assert.assertEquals(expecting, v.toString());
    }
    
    @Deprecated
    public static void evaluatePerf(TypedAST ast) {
        wyvern.tools.typedAST.interfaces.Value v = ast.evaluate(Globals.getStandardEvalEnv());
        //String expecting = "IntegerConstant(" + value + ")"; 
        //Assert.assertEquals(expecting, v.toString());
    }
    
    /**
     * First typechecks the AST, then executes it.
     * 
     * Any returned value is discarded, but anything printed to stdout will be visible.
     * 
     * @param ast
     */
    public static void evaluate(TypedAST ast) {
        ast.typecheck(Globals.getStandardEnv(), Optional.empty());
        ast.evaluate(Globals.getStandardEvalEnv());
    }
    
    /**
     * First typechecks the AST, then executes it.
     * If any file is loaded, it is parsed by the new parser.
     * 
     * Any returned value is returned, and anything printed to stdout will be visible.
     * 
     * @param ast
     */
    public static wyvern.tools.typedAST.interfaces.Value evaluateNew(TypedAST ast) {
        boolean oldParserFlag = WyvernResolver.getInstance().setNewParser(true);
        try {
            ast.typecheck(Globals.getStandardEnv(), Optional.empty());
            return ast.evaluate(Globals.getStandardEvalEnv());
        } finally {
            WyvernResolver.getInstance().setNewParser(oldParserFlag);
        }
    }
    
    public static String readFile(String filename) {
        return readFile(new File(filename));
    }
    
    public static String readFile(File file) {
        try {
            StringBuffer b = new StringBuffer();
            for (String s : Files.readAllLines(file.toPath())) {
                //Be sure to add the newline as well
                b.append(s).append("\n");
            }
            return b.toString();
        } catch (IOException e) {
            ToolError.reportError(ErrorMessage.READ_FILE_ERROR, (FileLocation) null, file.getPath());
            //Assert.fail("Failed opening file: " + file.getPath());
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Removes the global tagged-type data.
     */
    private static void clearGlobalTagInfo() {
        TaggedInfo.clearGlobalTaggedInfos();
    }

    public static void doTestScript(String fileName, ValueType expectedType, Value expectedValue) throws ParseException {
        String source = TestUtil.readFile(PATH + fileName);
        ExpressionAST ast = (ExpressionAST) TestUtil.getNewAST(source, "test input");
        InterpreterState state = new InterpreterState(InterpreterState.PLATFORM_JAVA, new File(TestUtil.BASE_PATH), null);
        GenContext genCtx = Globals.getGenContext(state);
        IExpr program = ast.generateIL(genCtx, null, new LinkedList<TypedModuleSpec>());
        doChecks(program, expectedType, expectedValue);
    }

    public static void doTestInt(String input, int expectedIntResult) throws ParseException {
        doTest(input, Util.intType(), new IntegerLiteral(expectedIntResult));
    }

    // TODO: make other string tests call this function
    public static void doTest(String input, ValueType expectedType, Value expectedResult) throws ParseException {
        ExpressionAST ast = (ExpressionAST) TestUtil.getNewAST(input, "test input");
        GenContext genCtx = Globals.getGenContext(new InterpreterState(InterpreterState.PLATFORM_JAVA,
                                                                   new File(TestUtil.BASE_PATH),
                                                                   new File(TestUtil.LIB_PATH)));
        final LinkedList<TypedModuleSpec> dependencies = new LinkedList<TypedModuleSpec>();
        IExpr program = ast.generateIL(genCtx, null, dependencies);
        program = genCtx.getInterpreterState().getResolver().wrap(program, dependencies);
        doChecks(program, expectedType, expectedResult);
    }

    // TODO: make other script tests call this function
    public static void doTestScriptModularly(String qualifiedName, ValueType expectedType, Value expectedValue) throws ParseException {
      InterpreterState state = new InterpreterState(InterpreterState.PLATFORM_JAVA,new File(TestUtil.BASE_PATH), new File(TestUtil.LIB_PATH));
      final Module module = state.getResolver().resolveModule(qualifiedName);
      IExpr program = state.getResolver().wrap(module.getExpression(), module.getDependencies());
      doChecks(program, expectedType, expectedValue);
    }
    
    private static void doChecks(IExpr program, ValueType expectedType, Value expectedValue) {
        // resolveModule already typechecked, but we'll do it again to verify the type
        TypeContext ctx = Globals.getStandardTypeContext();
        ValueType t = program.typeCheck(ctx);
        if (expectedType != null)
            Assert.assertEquals(expectedType, t);
        
        // check the result
        Value v = program.interpret(Globals.getStandardEvalContext());
        if (expectedValue != null)
            Assert.assertEquals(expectedValue, v);
    }

    public static void doTestModule(String input, String fieldName, ValueType expectedType, Value expectedValue) throws ParseException {
        TypedAST ast = TestUtil.getNewAST(input, "test input");
        GenContext genCtx = Globals.getGenContext(new InterpreterState(InterpreterState.PLATFORM_JAVA, null, null));
        TypeContext ctx = Globals.getStandardTypeContext();
        wyvern.target.corewyvernIL.decl.Declaration decl = ((Declaration) ast).topLevelGen(genCtx, new LinkedList<TypedModuleSpec>());
        IExpr mainProgram = ((DefDeclaration)decl).getBody();
        IExpr program = new FieldGet(mainProgram, fieldName, null); // slightly hacky       
        doChecks(program, expectedType, expectedValue);
    }

    public static void doTestTypeFail(String input) throws ParseException {
		ExpressionAST ast = (ExpressionAST) TestUtil.getNewAST(input, "test input");
		GenContext genCtx = Globals.getGenContext(new InterpreterState(InterpreterState.PLATFORM_JAVA, null, null));
		try {
			IExpr program = ast.generateIL(genCtx, null, new LinkedList<TypedModuleSpec>());
			program.typeCheck(Globals.getStandardTypeContext());
			Assert.fail("Typechecking should have failed.");
		} catch (ToolError e) {
		}
	}

}
