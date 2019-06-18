package wyvern.tools;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import wyvern.stdlib.Globals;
import wyvern.target.corewyvernIL.ASTNode;
import wyvern.target.corewyvernIL.astvisitor.PlatformSpecializationVisitor;
import wyvern.target.corewyvernIL.astvisitor.TailCallVisitor;
import wyvern.target.corewyvernIL.expression.IExpr;
import wyvern.target.corewyvernIL.modules.Module;
import wyvern.target.corewyvernIL.support.InterpreterState;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.tools.errors.ToolError;

public final class Interpreter {
    private Interpreter() { }
    /**
     * The interpreter only supports 1 argument, which is the path to the Wyvern
     * file. If more arguments are supplied, it will exit with an error. Then,
     * the file is read in to memory in it's entirety, before being executed in
     * an empty context. The resulting value is printed to the screen.
     */
    public static void main(String[] args) {
        // check if 1 argument is supplied.
        if (args.length != 1) {
            System.err.println("usage: wyvern <filename>");
            System.exit(1);
        }
        String filename = args[0];
        Path filepath = Paths.get(filename);

        // check if file is readable.
        if (!Files.isReadable(filepath)) {
            System.err.println("Cannot read file " + filename);
            System.exit(1);
        }
        try {
            // obtain WYVERN_ROOT environment variable.
            String rootLoc = System.getenv("WYVERN_ROOT");
            if (wyvernRoot.get() != null) {
                rootLoc = wyvernRoot.get();
            } else {
                rootLoc = System.getProperty("user.dir");
            }

            // create file object using root location.
            File rootDir = new File(rootLoc);

            // obtain WYVERN_HOME environment variable.
            String wyvernPath = System.getenv("WYVERN_HOME");

            // check and obtain the WYVERN_HOME environment variable.
            if (wyvernPath == null) {
                if (wyvernHome.get() != null) {
                    wyvernPath = wyvernHome.get();
                } else {
                    System.err.println("must set WYVERN_HOME environmental variable to wyvern project directory");
                    return;
                }
            }

            // go to WYVERN_ROOT/stdlib
            wyvernPath += "/stdlib/";

            // sanity check: is the wyvernPath a valid directory?
            if (!Files.isDirectory(Paths.get(wyvernPath))) {
                System.err.println("Error: WYVERN_HOME is not set to a valid Wyvern project directory");
                return;
            }

            // create interpreter state using Java platform
            final InterpreterState state = new InterpreterState(InterpreterState.PLATFORM_JAVA, rootDir, new File(wyvernPath));
            
            // resolve modules and create Module object
            Module m = state.getResolver().load("unknown", filepath.toFile(), true);
            IExpr program = m.getExpression();

            program = state.getResolver().wrap(program, m.getDependencies());
            program = (IExpr) PlatformSpecializationVisitor.specializeAST((ASTNode) program, "java", Globals.getGenContext(state));

            /*ExpressionAST ast = (ExpressionAST) TestUtil.getNewAST(filepath.toFile());
            GenContext genCtx = Globals.getGenContext(state);
            Expression program = ast.generateIL(genCtx, null);*/
            TypeContext ctx = Globals.getStandardTypeContext();
            program.typecheckNoAvoidance(ctx, null);
            TailCallVisitor.annotate(program);
            program.interpret(Globals.getStandardEvalContext());
        /*} catch (ParseException e) {
            System.err.println("Parse error: " + e.getMessage());*/
        } catch (ToolError e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    // used to set WYVERN_HOME when called programmatically
    public static final ThreadLocal<String> wyvernHome = new ThreadLocal<String>();

    // used to set WYVERN_ROOT when called programmatically
    public static final ThreadLocal<String> wyvernRoot = new ThreadLocal<String>();

}
