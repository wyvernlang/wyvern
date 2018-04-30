package wyvern.tools.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import wyvern.stdlib.Globals;
import wyvern.target.corewyvernIL.astvisitor.EmitOIRState;
import wyvern.target.corewyvernIL.astvisitor.EmitOIRVisitor;
import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.expression.IExpr;
import wyvern.target.corewyvernIL.modules.TypedModuleSpec;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.oir.EmitPythonVisitor;
import wyvern.target.oir.OIRAST;
import wyvern.target.oir.OIREnvironment;
import wyvern.tools.imports.extensions.WyvernResolver;
import wyvern.tools.parsing.coreparser.ParseException;
import wyvern.tools.tests.suites.RegressionTests;
import wyvern.tools.typedAST.interfaces.ExpressionAST;

@Category(RegressionTests.class)
public class FFITests {

    private static final String BASE_PATH = TestUtil.BASE_PATH;
    private static final String PATH = BASE_PATH + "modules/module/";

    @BeforeClass public static void setupResolver() {
        TestUtil.setPaths();
        WyvernResolver.getInstance().addPath(PATH);
    }

    private void testPyFromInput(String input, String expected) throws ParseException {
        testPyFromInput(input, expected, false);
    }

    private void testPyFromInput(String input, String expected, boolean debug) throws ParseException {
        // Since the root OIR environment is stateful, reset it between tests
        OIREnvironment.resetRootEnvironment();
        ExpressionAST ast = (ExpressionAST) TestUtil.getNewAST(input, "test input");
        GenContext genContext = Globals.getStandardGenContext();
        IExpr iLprogram = ast.generateIL(genContext, null, new LinkedList<TypedModuleSpec>());
        if (debug) {
            System.out.println("Wyvern Program:");
            System.out.println(input);
            try {
                System.out.println("IL program:\n" + ((Expression) iLprogram).prettyPrint());
            } catch (IOException e) {
                System.err.println("Error pretty-printing IL program.");
            }
        }
        OIRAST oirast =
                iLprogram.acceptVisitor(new EmitOIRVisitor(),
                        new EmitOIRState(Globals.getStandardTypeContext(),
                                OIREnvironment.getRootEnvironment()));

        String pprint =
                new EmitPythonVisitor().emitPython(oirast,
                        OIREnvironment.getRootEnvironment(), true);

        if (debug) {
            System.out.println("OIR Program:\n" + pprint);
        }

        // Call the system python interpreter to execute the code
        try {
            File tempFile = File.createTempFile("wyvern", ".py");
            tempFile.deleteOnExit();

            FileWriter fw = new FileWriter(tempFile);
            fw.write(pprint);
            fw.close();

            Process p = Runtime.getRuntime().exec("python " + tempFile.getAbsolutePath());

            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
            BufferedReader stdErr = new BufferedReader(new InputStreamReader(p.getErrorStream()));

            if (debug) {
                System.out.println("Python output:");
            }

            if (!p.waitFor(10, TimeUnit.SECONDS)) {
                System.out.println("Python code timed out!");
                fail("Python timeout -- infinite loop, or just slow?");
                return;
            }

            String result = "";
            String s = null;
            while ((s = stdInput.readLine()) != null) {
                if (debug) {
                    System.out.println(s);
                }
                if (result != "") {
                    result += "\n";
                }
                result += s;
            }

            if (debug) {
                System.out.println("Python error output:");
                while ((s = stdErr.readLine()) != null) {
                    System.out.println(s);
                }
            }
            assertEquals(expected, result);
        } catch (Exception e) {
            System.out.println("Error running python test: " + e.toString());
        }
    }

    @Test
    public void testPythonImport() throws ParseException {
        String input =
                "import python:math\n\n"
                      + "type MathType\n"
                      + "    def factorial(x : system.Int) : system.Int\n"
                      + "    def pow(x : system.Int, y : system.Int) : system.Int\n"
                      + "    val pi : system.Int\n"
                      + "val m : MathType = math\n"
                      + "val x : system.Int = m.pow(2, 8)\n"
                      + "x\n";
        testPyFromInput(input, "256.0");
    }

    @Test
    public void testPythonMultipleImports() throws ParseException {
        String input =
                "import python:math\n"
                      + "import python:json\n"
                      + "type JsonType\n"
                      + "  def dumps(x : system.Int) : system.String\n"
                      + "type MathType\n"
                      + "  def factorial(x : system.Int) : system.Int\n"
                      + "val j : JsonType = json\n"
                      + "val m : MathType = math\n"
                      + "j.dumps(m.factorial(5))\n";
        testPyFromInput(input, "120");
    }

}
