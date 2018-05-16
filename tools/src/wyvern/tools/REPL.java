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
import wyvern.target.corewyvernIL.VarBinding;
import wyvern.target.corewyvernIL.astvisitor.PlatformSpecializationVisitor;
import wyvern.target.corewyvernIL.astvisitor.TailCallVisitor;
import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.expression.IExpr;
import wyvern.target.corewyvernIL.expression.SeqExpr;
import wyvern.target.corewyvernIL.expression.Value;
import wyvern.target.corewyvernIL.modules.Module;
import wyvern.target.corewyvernIL.modules.TypedModuleSpec;
import wyvern.target.corewyvernIL.support.EvalContext;
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
import wyvern.tools.util.Pair;

public class REPL {
	public static final String WYVERN_HOME = System.getenv("WYVERN_HOME");
    public static final String BASE_PATH = WYVERN_HOME == null
            ? "src/wyvern/tools/tests/" : WYVERN_HOME + "/tools/src/wyvern/tools/tests/";
    public static final String STDLIB_PATH = BASE_PATH + "stdlib/";
    public static final String LIB_PATH = WYVERN_HOME == null ? "../stdlib/" : WYVERN_HOME + "/stdlib/";
    public static final String EXAMPLES_PATH = WYVERN_HOME == null ? "../examples/" : WYVERN_HOME + "/examples/";
    private static final String PLATFORM_PATH = BASE_PATH + "platform/java/stdlib/";
    
    private static EvalContext programContext = null;
    //private static IExpr program = null;
	
	public REPL() {
		
	}
	public static void main(String[] args) throws Exception {
		String input = "" // "require stdout\n\n"
		        + "val x = \"kjsadfka\"\n"
		        + "val y = 3\n";
		
		interepetProgram(input);
		
    }
	
	public static Value interepetProgram(String input) throws ParseException {
	    String lines[] = input.split("\\r?\\n");
	    Value currentResult = null;
	    for (String s: lines) {
	        if (!(s.length()==0)) { //does not run code on empty lines
	            if (s.split(" ").length == 1 && programContext != null) { //assuming when user enters 1 word it refers to a val
	                Value result = programContext.lookupValue(s);
	                return result;
	            } else {
	                String[] line = s.split(" ");
	                if (line[0].equals("val") && programContext != null ) { // programContext is not empty, and expression is a variable declaration
	                    System.out.println("got here");
	                    Value result = programContext.lookupValue(line[1]); // checks if there is an already existing variable in the context with the same name.
	                    if (result == null) {
	                        System.out.println("there was not variable named that");
	                    }
	                } else if (line[0].equals("var") && programContext != null) {
	                    System.out.println("shouldnt be here");
	                }
	                System.out.println("STARTED PROCESSING: " + s);
                    currentResult = parseVar(s + "\n");
                    System.out.println("FINISHED PROCESSING: " + s);
	            }
	        }
	    }
	    return currentResult;
	}
	
	public static Value parseVar(String input) throws ParseException {
	    if (programContext == null){
	        //System.out.println("Inside OR, btw with pC = " + programContext + " p = " + program);
	        programContext = Globals.getStandardEvalContext();
	        ExpressionAST ast = (ExpressionAST) getNewAST(input, "test input");
	        GenContext genCtx = Globals.getGenContext(new InterpreterState(InterpreterState.PLATFORM_JAVA,
	                new File(BASE_PATH),
	                new File(LIB_PATH)));
	        final LinkedList<TypedModuleSpec> dependencies = new LinkedList<TypedModuleSpec>();
	        IExpr program =  ast.generateIL(genCtx, null, dependencies);
	        program = genCtx.getInterpreterState().getResolver().wrap(program, dependencies);
	        
	        Pair<Value, EvalContext> result = ((SeqExpr) program).interpretCtx(programContext);
            programContext = result.getSecond();
            System.out.println(programContext );
            System.out.println(result.getFirst());
            return result.getFirst();
	    }else {
	       
	        ExpressionAST ast = (ExpressionAST) getNewAST(input, "test input");
	        GenContext genCtx = Globals.getGenContext(new InterpreterState(InterpreterState.PLATFORM_JAVA,
	                new File(BASE_PATH),
	                new File(LIB_PATH)));
	        final LinkedList<TypedModuleSpec> dependencies = new LinkedList<TypedModuleSpec>();
	        IExpr program =  ast.generateIL(genCtx, null, dependencies);
	        program = genCtx.getInterpreterState().getResolver().wrap(program, dependencies);
	        
	        Pair<Value, EvalContext> result = ((SeqExpr) program).interpretCtx(programContext);
            programContext = result.getSecond();
	        System.out.println(programContext);
	        //System.out.println(result.getFirst());
	        return result.getFirst();
	        
	        //System.out.println(programContext.lookupValue("x"));
	    }
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
