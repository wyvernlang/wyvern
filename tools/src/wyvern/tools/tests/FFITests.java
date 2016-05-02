package wyvern.tools.tests;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import wyvern.target.corewyvernIL.astvisitor.EmitOIRVisitor;
import wyvern.target.corewyvernIL.decltype.DeclType;
import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.expression.FieldGet;
import wyvern.target.corewyvernIL.expression.IntegerLiteral;
import wyvern.target.corewyvernIL.expression.Let;
import wyvern.target.corewyvernIL.expression.StringLiteral;
import wyvern.target.corewyvernIL.expression.Value;
import wyvern.target.corewyvernIL.expression.Variable;
import wyvern.target.corewyvernIL.support.EvalContext;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.support.GenUtil;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.support.TypeGenContext;
import wyvern.target.corewyvernIL.support.Util;
import wyvern.target.corewyvernIL.type.NominalType;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.target.oir.OIRAST;
import wyvern.target.oir.OIREnvironment;
import wyvern.target.oir.PrettyPrintVisitor;
import wyvern.tools.Interpreter;
import wyvern.tools.errors.ToolError;
import wyvern.tools.imports.extensions.WyvernResolver;
import wyvern.tools.interop.FObject;
import wyvern.tools.parsing.coreparser.ParseException;
import wyvern.tools.tests.suites.CurrentlyBroken;
import wyvern.tools.tests.suites.RegressionTests;
import wyvern.tools.tests.tagTests.TestUtil;
import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.core.Sequence;
import wyvern.tools.typedAST.interfaces.ExpressionAST;
import wyvern.tools.typedAST.interfaces.TypedAST;

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
    ExpressionAST ast = (ExpressionAST) TestUtil.getNewAST(input);
    GenContext genContext = TestUtil.getStandardGenContext();
    Expression ILprogram = ast.generateIL(genContext, null);
    if (debug) {
      System.out.println("Wyvern Program:");
      System.out.println(input);
      try {
        System.out.println("IL program:\n" + ILprogram.prettyPrint());
      } catch (IOException e) {
        System.err.println("Error pretty-printing IL program.");
      }
    }
    OIRAST oirast =
      ILprogram.acceptVisitor(new EmitOIRVisitor(),
                              null,
                              OIREnvironment.getRootEnvironment());

    String pprint =
      new PrettyPrintVisitor().prettyPrint(oirast,
                                           OIREnvironment.getRootEnvironment());

    if (debug)
      System.out.println("OIR Program:\n" + pprint);

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

      if (debug)
        System.out.println("Python output:");

      if (!p.waitFor(10, TimeUnit.SECONDS)) {
        System.out.println("Python code timed out!");
        fail("Python timeout -- infinite loop, or just slow?");
        return;
      }

      String result = "";
      String s = null;
      while ((s = stdInput.readLine()) != null) {
        if (debug)
          System.out.println(s);
        if (result != "")
          result += "\n";
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
      "import python:math\n\n" +
      "type MathType\n" +
      "    def factorial(x : system.Int) : system.Int\n" +
      "    def pow(x : system.Int, y : system.Int) : system.Int\n" +
      "    val pi : system.Int\n" +
      "val m : MathType = math\n" +
      "val x : system.Int = m.pow(2, 8)\n" +
      "x\n";
    testPyFromInput(input, "256.0");
  }

  @Test
  public void testPythonMultipleImports() throws ParseException {
    String input =
      "import python:math\n" +
      "import python:json\n" +
      "type JsonType\n" +
      "  def dumps(x : system.Int) : system.String\n" +
      "type MathType\n" +
      "  def factorial(x : system.Int) : system.Int\n" +
      "val j : JsonType = json\n" +
      "val m : MathType = math\n" +
      "j.dumps(m.factorial(5))\n";
    testPyFromInput(input, "120");
  }

}
