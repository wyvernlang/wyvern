package wyvern.tools;

import java.io.File;
import java.io.Reader;
import java.io.StringReader;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;

import com.sun.net.httpserver.HttpServer;

import wyvern.stdlib.Globals;
import wyvern.target.corewyvernIL.ASTNode;
import wyvern.target.corewyvernIL.astvisitor.PlatformSpecializationVisitor;
import wyvern.target.corewyvernIL.astvisitor.TailCallVisitor;
import wyvern.target.corewyvernIL.expression.IExpr;
import wyvern.target.corewyvernIL.modules.Module;
import wyvern.target.corewyvernIL.modules.TypedModuleSpec;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.support.InterpreterState;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.tools.ReplServer.MyHandler;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.errors.ToolError;
import wyvern.tools.parsing.coreparser.ParseException;
import wyvern.tools.parsing.coreparser.ParseUtils;
import wyvern.tools.parsing.coreparser.Token;
import wyvern.tools.parsing.coreparser.WyvernParser;
import wyvern.tools.parsing.coreparser.WyvernParserConstants;
import wyvern.tools.typedAST.core.expressions.TaggedInfo;
import wyvern.tools.typedAST.interfaces.ExpressionAST;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.types.Type;

public class REPL {
	public static final String WYVERN_HOME = System.getenv("WYVERN_HOME");
    public static final String BASE_PATH = WYVERN_HOME == null
            ? "src/wyvern/tools/tests/" : WYVERN_HOME + "/tools/src/wyvern/tools/tests/";
    public static final String STDLIB_PATH = BASE_PATH + "stdlib/";
    public static final String LIB_PATH = WYVERN_HOME == null ? "../stdlib/" : WYVERN_HOME + "/stdlib/";
    public static final String EXAMPLES_PATH = WYVERN_HOME == null ? "../examples/" : WYVERN_HOME + "/examples/";
    private static final String PLATFORM_PATH = BASE_PATH + "platform/java/stdlib/";
	
	public REPL() {
		
	}
	public static void main(String[] args) throws Exception {
		String input = "require stdout\n\n"
                + "val x = \"Hello, \"\n";
		
		parseVar(input);
    }
	
	public static void parseVar(String input) throws ParseException {
		ExpressionAST ast = (ExpressionAST) getNewAST(input, "test input");
        GenContext genCtx = Globals.getGenContext(new InterpreterState(InterpreterState.PLATFORM_JAVA,
                new File(BASE_PATH),
                new File(LIB_PATH)));
        final LinkedList<TypedModuleSpec> dependencies = new LinkedList<TypedModuleSpec>();
        IExpr program = ast.generateIL(genCtx, null, dependencies);
        program = genCtx.getInterpreterState().getResolver().wrap(program, dependencies);
        //program.interpret(Globals.getStandardEvalContext());
        //System.out.println(program.toString());
	}
	
	
	public static TypedAST getNewAST(String program, String programName) throws ParseException {
        clearGlobalTagInfo();
        Reader r = new StringReader(program);
        WyvernParser<TypedAST, Type> wp = ParseUtils.makeParser(programName, r);
        TypedAST result = wp.CompilationUnit();
        final Token nextToken = wp.token_source.getNextToken();
        if (nextToken.kind != WyvernParserConstants.EOF) {
            ToolError.reportError(ErrorMessage.UNEXPECTED_INPUT, wp.loc(nextToken));
        }
        return result;
    }
	
	private static void clearGlobalTagInfo() {
        TaggedInfo.clearGlobalTaggedInfos();
    }
	
	 // used to set WYVERN_HOME when called programmatically
    public static final ThreadLocal<String> wyvernHome = new ThreadLocal<String>();

    // used to set WYVERN_ROOT when called programmatically
    public static final ThreadLocal<String> wyvernRoot = new ThreadLocal<String>();
}
