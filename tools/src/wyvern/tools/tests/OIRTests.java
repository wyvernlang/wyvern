package wyvern.tools.tests;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.IOException;
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
public class OIRTests {

	private static final String BASE_PATH = TestUtil.BASE_PATH;
	private static final String PATH = BASE_PATH + "modules/module/";

  @BeforeClass public static void setupResolver() {
    TestUtil.setPaths();
		WyvernResolver.getInstance().addPath(PATH);
  }

  private void printPyFromInput(String input, String expected) throws ParseException {
    printPyFromInput(input, expected, false);
  }

  private void printPyFromInput(String input, String expected, boolean debug) throws ParseException {
    ExpressionAST ast = (ExpressionAST) TestUtil.getNewAST(input);
    Expression ILprogram = ast.generateIL(GenContext.empty().extend("system", new Variable("system"), null), null);
    if (debug) {
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
    if (debug) {
      System.out.println("OIR Root Environment:");
      System.out.println(OIREnvironment.getRootEnvironment().prettyPrint());
    }
    String pprint =
      new PrettyPrintVisitor().prettyPrint(oirast,
                                           OIREnvironment.getRootEnvironment());

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

      System.out.println("Python output:");

      String result = "";
      String s = null;
      while ((s = stdInput.readLine()) != null) {
        System.out.println(s);
        if (result != "")
          result += "\n";
        result += s;
      }

      System.out.println("Python error output:");
      while ((s = stdErr.readLine()) != null) {
        System.out.println(s);
      }

      assertEquals(expected, result);
    } catch (IOException e) {
      System.out.println("Error running python test: " + e.toString());
    }
  }

  @Test
  public void testOIRLetValWithParse() throws ParseException {
    String input =
      "val x = 5\n" +
      "x\n";
    printPyFromInput(input, "5");
  }

  @Test
  public void testMultipleLets() throws ParseException {
    String input =
      "val x = 5\n" +
      "val y = 7\n" +
      "x\n";
    printPyFromInput(input, "5");
  }

  @Test
  public void testOIRLetValWithString() throws ParseException {
    String input =
      "val x = \"five\"\n" +
      "x\n";
    printPyFromInput(input, "five");
  }

  @Test
  public void testOIRLetValWithString3() throws ParseException {
    String input =
      "val identity = (x: system.Int) => x\n" +
      "identity(5)";
    printPyFromInput(input, "5");
  }

  @Test
  public void testOIRFieldRead() throws ParseException {
    String input =
      "val obj = new\n" +
      "    val v = 5\n" +
      "obj.v\n";
    printPyFromInput(input, "5");
  }

  @Test
  public void testOIRMultipleFields() throws ParseException {
    String input =
      "val obj = new\n" +
      "    val x = 23\n" +
      "    val y = 64\n" +
      "obj.y\n";
    printPyFromInput(input, "64");
  }

  @Test
  public void testOIRVarFieldRead() throws ParseException {
    String input =
      "val obj = new\n" +
      "    var v : system.Int = 5\n" +
      "obj.v\n";
    printPyFromInput(input, "5");
  }

  @Test
  public void testOIRVarFieldWrite() throws ParseException {
    String input =
      "val obj = new\n" +
      "    var v : system.Int = 2\n" +
      "obj.v = 23\n" +
      "obj.v\n";
    printPyFromInput(input, "23");
  }

  @Test
  public void testDefWithVarInside() throws ParseException {
    String input =
      "def foo() : system.Int\n" +
      "    var v : system.Int = 5\n" +
      "    v = 10\n" +
      "    v\n" +
      "foo()\n";
    printPyFromInput(input, "10", true);
  }
}
