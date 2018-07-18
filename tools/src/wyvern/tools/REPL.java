package wyvern.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Console;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.Scanner;

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
import wyvern.target.corewyvernIL.support.TopLevelContext;
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
import wyvern.tools.typedAST.core.Script;

public class REPL {
    public static final String WYVERN_HOME = System.getenv("WYVERN_HOME");
    public static final String BASE_PATH = WYVERN_HOME == null ? "src/wyvern/tools/tests/"
            : WYVERN_HOME + "/tools/src/wyvern/tools/tests/";
    public static final String STDLIB_PATH = BASE_PATH + "stdlib/";
    public static final String LIB_PATH = WYVERN_HOME == null ? "../stdlib/" : WYVERN_HOME + "/stdlib/";
    public static final String EXAMPLES_PATH = WYVERN_HOME == null ? "../examples/" : WYVERN_HOME + "/examples/";
    private static final String PLATFORM_PATH = BASE_PATH + "platform/java/stdlib/";

    private EvalContext programContext;
    private String tempCode = "";
    private GenContext genContext;

    public REPL() {

    }

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        String input = "";
        REPL repl = new REPL();

        while (true) {
            input = scanner.nextLine();
            Value v = repl.interpretREPL(input);
            if (v != null) {
                System.out.println(v);
            }
        }
    }
    
    /**
     * Method for interpreting user input from command line to be processed by the REPL
     * 
     * @param  the inputed code from the user.
     * @return The result of the code being interpreted.
     */
    public Value interpretREPL(String userInput) {
        try {
            if (userInput.equals("exit")) {
                System.exit(1);
            } else if (userInput.equals("genctx")) {
                System.out.println(genContext);
            } else if (userInput.equals("evalctx")) {
                System.out.println(programContext);
            } else if (userInput.equals("clear")) {
                tempCode = "";
            } else if (userInput.equals("reset")) {
                programContext = null;
                genContext = null;
                tempCode = "";
            } else if (userInput.equals("code")) {
                System.out.println(tempCode);
            } else {
                Value v = parse(userInput);
                if (v != null) {
                    return v;
                }
            }
        } catch (Exception e) {
            //if error is thrown, code is stored and re-run until a 
            //correct line of code in entered to conplete a block, e.g functions
            tempCode = tempCode + userInput + "\n";
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Method for processing user input after it has been processed to be process by the interpretREPL method,
     * this method prepares the code to be processed by the Wyvern interpreter
     * 
     * @param  The input that has been processed by interpretREPL
     * @return The result of the code being interpreted from the Wyvern interperter
     */
    public Value parse(String input) throws ParseException {
        String lines[] = input.split("\\r?\\n");
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
                }
            }
        }
        return currentResult;
    }
    
    /**
     * This method runs the code that has been processed through the Wyvern Compiler
     * 
     * @param  The code that will be run
     * @return The result of the code
     */
    public Value updateCode(String input) throws ParseException {
        if (input.length() == 0) {
            // sanity check
            return null;
        }
        if (programContext == null || genContext == null) // first line of code interpreted before program context and
                                                          // gen context have been created
        {

            programContext = Globals.getStandardEvalContext();
            ExpressionAST ast = (ExpressionAST) getNewAST(input, "test input");

            GenContext genCtx = Globals.getGenContext(
                    new InterpreterState(InterpreterState.PLATFORM_JAVA, new File(BASE_PATH), new File(LIB_PATH)));

            final LinkedList<TypedModuleSpec> dependencies = new LinkedList<TypedModuleSpec>();

            TopLevelContext TLC = ((Script) ast).generateTLC(genCtx, null, dependencies);

            SeqExpr program = (SeqExpr) TLC.getExpression();
            program = genCtx.getInterpreterState().getResolver().wrap(program, dependencies);
            Pair<Value, EvalContext> result = program.interpretCtx(programContext);

            programContext = result.getSecond();
            genContext = TLC.getContext();
            tempCode = "";
            return result.getFirst();
        } else // program already exists - extend existing context with new code
        {
            ExpressionAST ast = (ExpressionAST) getNewAST(input, "test input");
            final LinkedList<TypedModuleSpec> dependencies = new LinkedList<TypedModuleSpec>();

            TopLevelContext TLC = ((Script) ast).generateTLC(genContext, null, dependencies);

            SeqExpr program = (SeqExpr) TLC.getExpression();
            program = genContext.getInterpreterState().getResolver().wrap(program, dependencies);

            System.out.println("Interpreting:");

            Pair<Value, EvalContext> result = program.interpretCtx(programContext);

            programContext = result.getSecond();

            genContext = program.extendContext(TLC.getContext());

            tempCode = "";
            return result.getFirst();
        }
    }
    
    /**
     * Method for generating new Abstract syntax tree
     * 
     * @param  The Wyvern code that will be used to create the AST
     * @return The generated AST
     */
    public TypedAST getNewAST(String program, String programName) throws ParseException {
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

    private void clearGlobalTagInfo() {
        TaggedInfo.clearGlobalTaggedInfos();
    }

    // used to set WYVERN_HOME when called programmatically
    public final ThreadLocal<String> wyvernHome = new ThreadLocal<String>();

    // used to set WYVERN_ROOT when called programmatically
    public final ThreadLocal<String> wyvernRoot = new ThreadLocal<String>();
}
