package wyvern.tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import wyvern.stdlib.Globals;
import wyvern.target.corewyvernIL.ASTNode;
import wyvern.target.corewyvernIL.astvisitor.EmitOIRState;
import wyvern.target.corewyvernIL.astvisitor.EmitOIRVisitor;
import wyvern.target.corewyvernIL.astvisitor.PlatformSpecializationVisitor;
import wyvern.target.corewyvernIL.astvisitor.TailCallVisitor;
import wyvern.target.corewyvernIL.expression.IExpr;
import wyvern.target.corewyvernIL.modules.Module;
import wyvern.target.corewyvernIL.support.InterpreterState;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.support.Util;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.target.oir.EmitPythonVisitor;
import wyvern.target.oir.OIRAST;
import wyvern.target.oir.OIREnvironment;
import wyvern.tools.errors.ToolError;
import wyvern.tools.parsing.coreparser.ParseException;
import wyvern.tools.tests.TestUtil;
import wyvern.tools.typedAST.interfaces.TypedAST;

/** Compiles to Python source code.  Should work with either Python 2 or Python 3.
 */
public final class PythonCompiler {
    private PythonCompiler() { }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("usage: wypy FILENAME [-o OUTPUT_FILE] [-dIL] [-dAST]");
            System.exit(1);
        }
        String filename = null;
        String outputFile = null;
        boolean displayIL = false;
        boolean displayAST = false;

        for (int i = 0; i < args.length; i++) {
            if ("-o".equals(args[i])) {
                if (args.length < i + 2) {
                    System.err.println("Missing argument for -o");
                    System.exit(1);
                }
                outputFile = args[i + 1];
                i++;
            } else if ("-dIL".equals(args[i])) {
                displayIL = true;
            } else if ("-dAST".equals(args[i])) {
                displayAST = true;
            } else {
                if (filename == null) {
                    filename = args[i];
                } else {
                    System.err.println("Too many filenames given");
                    System.exit(1);
                }
            }
        }

        if (filename == null) {
            System.err.println("No input file given");
            System.exit(1);
        }
        if (outputFile == null) {
            outputFile = filename + ".py";
        }

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
            File rootDir = new File(rootLoc);
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
            final InterpreterState state = new InterpreterState(InterpreterState.PLATFORM_PYTHON, rootDir, new File(wyvernPath));
            if (displayAST) {
                try {
                    TypedAST ast = TestUtil.getNewAST(filepath.toFile());
                    System.out.println("AST:\n" + ast.prettyPrint().toString());
                } catch (ParseException e) {
                    System.out.println("Could not print AST");
                }
            }
            Module m = state.getResolver().load("unknown", filepath.toFile(), true);
            IExpr program = m.getExpression();
            program = state.getResolver().wrapForPython(program, m.getDependencies());
            program = (IExpr) PlatformSpecializationVisitor.specializeAST((ASTNode) program, "python", Globals.getGenContext(state));
            TailCallVisitor.annotate(program);

            if (displayIL) {
                StringBuilder buffer = new StringBuilder();
                try {
                    program.doPrettyPrint(buffer, "");
                } catch (IOException e) {
                    System.err.println("Error prettyprinting IL: " + e);
                    System.exit(1);
                }
                System.out.println(buffer.toString());
            }

            TypeContext ctx = Globals.getStandardTypeContext();
            ValueType type = program.typecheckNoAvoidance(ctx, null);
            boolean printResult = (type.equals(Util.unitType()));

            OIRAST oirast =
                    program.acceptVisitor(new EmitOIRVisitor(),
                            new EmitOIRState(Globals.getStandardTypeContext(),
                                    OIREnvironment.getRootEnvironment()));
            String python =
                    new EmitPythonVisitor().emitPython(oirast,
                            OIREnvironment.getRootEnvironment(), printResult);

            try (PrintWriter out = new PrintWriter(outputFile)) {
                out.println(python);
            } catch (FileNotFoundException e) {
                System.err.println("Could not write to file " + outputFile + ", " + e);
            }
            /*} catch (ParseException e) {
      System.err.println("Parse error: " + e.getMessage());*/
        } catch (ToolError e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    // used to set WYVERN_HOME when called programatically
    public static final ThreadLocal<String> wyvernHome = new ThreadLocal<String>();

    // used to set WYVERN_ROOT when called programatically
    public static final ThreadLocal<String> wyvernRoot = new ThreadLocal<String>();
}
