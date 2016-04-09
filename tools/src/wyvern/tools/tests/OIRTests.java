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
public class OIRTests {

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
      System.out.println("IL program output:\n" + ILprogram.interpret(EvalContext.empty()));
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
  public void testOIRLetValWithParse() throws ParseException {
    String input =
      "val x = 5\n" +
      "x\n";
    testPyFromInput(input, "5");
  }

  @Test
  public void testMultipleLets() throws ParseException {
    String input =
      "val x = 5\n" +
      "val y = 7\n" +
      "x\n";
    testPyFromInput(input, "5");
  }

  @Test
  public void testOIRLetValWithString() throws ParseException {
    String input =
      "val x = \"five\"\n" +
      "x\n";
    testPyFromInput(input, "five");
  }

  @Test
  public void testOIRLetValWithString3() throws ParseException {
    String input =
      "val identity = (x: system.Int) => x\n" +
      "identity(5)";
    testPyFromInput(input, "5");
  }

  @Test
  public void testOIRFieldRead() throws ParseException {
    String input =
      "val obj = new\n" +
      "    val v = 5\n" +
      "obj.v\n";
    testPyFromInput(input, "5");
  }

  @Test
  public void testOIRMultipleFields() throws ParseException {
    String input =
      "val obj = new\n" +
      "    val x = 23\n" +
      "    val y = 64\n" +
      "obj.y\n";
    testPyFromInput(input, "64");
  }

  @Test
  public void testOIRVarFieldRead() throws ParseException {
    String input =
      "val obj = new\n" +
      "    var v : system.Int = 5\n" +
      "obj.v\n";
    testPyFromInput(input, "5");
  }

  @Test
  public void testOIRVarFieldWrite() throws ParseException {
    String input =
      "val obj = new\n" +
      "    var v : system.Int = 2\n" +
      "obj.v = 23\n" +
      "obj.v\n";
    testPyFromInput(input, "23");
  }

  @Test
  public void testDefWithVarInside() throws ParseException {
    String input =
      "def foo() : system.Int\n" +
      "    var v : system.Int = 5\n" +
      "    v = 10\n" +
      "    v\n" +
      "foo()\n";
    testPyFromInput(input, "10", false);
  }

  @Test
  public void testDefWithValInside() throws ParseException {
    String input =
      "def foo() : system.Int\n" +
      "    val v : system.Int = 17\n" +
      "    v\n" +
      "foo()\n";
    testPyFromInput(input, "17");
  }

  @Test
  public void testDefDecl() throws ParseException {
    String input =
      "val obj = new\n" +
      "    val v : system.int = 5\n" +
      "    def m() : system.Int = 5\n" +
      "obj.v\n";
    testPyFromInput(input, "5");
  }

  @Test
  public void testIdentityCall() throws ParseException {
    String input =
      "val obj = new\n" +
      "    def id(x:system.Int) : system.Int = x\n" +
      "obj.id(13)\n";
    testPyFromInput(input, "13");
  }

  @Test
  public void testIdentityCallString() throws ParseException {
    String input =
      "val obj = new\n" +
      "    def id(x:system.String) : system.String = x\n" +
      "obj.id(\"Well met!\")\n";
    testPyFromInput(input, "Well met!");
  }

  @Test
  public void testType() throws ParseException {
    String input =
      "type IntResult\n" +
      "    def getResult() : system.Int\n\n" +
      "val r : IntResult = new\n" +
      "    def getResult() : system.Int = 18\n\n" +
      "r.getResult()\n";
    testPyFromInput(input, "18");
  }

  @Test
  public void testTypeAbbrev() throws ParseException {
    String input =
      "type Int = system.Int\n" +
      "val i : Int = 32\n" +
      "i\n";
    testPyFromInput(input, "32");
  }

  @Test
  public void testSimpleDelegation() throws ParseException {
    String input =
      "type IntResult\n" +
      "    def getResult() : system.Int\n" +
      "val r : IntResult = new\n" +
      "    def getResult() : system.Int = 26\n" +
      "val r2 : IntResult = new\n" +
      "    delegate IntResult to r\n" +
      "r2.getResult()\n";
    testPyFromInput(input, "26");
  }

  @Test
  public void declareRecursiveFunction() throws ParseException {
    String input =
      "val f : system.Int = 3\n" +
      "def m(y : system.Int) : system.Int = m(y)\n" +
      "f\n";
    testPyFromInput(input, "3");
  }

  @Test
  public void testScoping() throws ParseException {
    String input =
      "def f(x : system.Int) : system.Int\n" +
      "    val obj = new\n" +
      "        def const() : system.Int = x\n" +
      "    obj.const()\n" +
      "f(7)\n";
    testPyFromInput(input, "7");
  }

  @Test
  public void testAssignAsExpression() throws ParseException {
    String input =
      "val obj = new\n" +
      "    var x : system.Int = 3\n" +
      "    def identity(x : system.Int) : system.Int = x\n" +
      "obj.identity(obj.x = 7)\n" +
      "obj.x\n";
    testPyFromInput(input, "7");
  }

  @Test
  public void testIfStatement() throws ParseException {
    String input = ""
      + "type Body\n"
      + "    type T = system.Int\n"
      + "    def apply(): this.T \n\n"

      + "type Boolean\n"
      + "   def iff(thenFn: Body, elseFn: Body) : thenFn.T \n\n"

      + "val true = new \n"
      + "    def iff(thenFn: Body, elseFn: Body): thenFn.T \n\n"
      + "        thenFn.apply()\n\n"

      + "val false = new \n"
      + "    def iff(thenFn: Body, elseFn: Body): thenFn.T \n"
      + "        elseFn.apply()\n\n"

      + "def ifSt(bool: Boolean, thenFn: Body, elseFn: Body): thenFn.T \n"
      + "    bool.iff(thenFn, elseFn) \n\n"

      + "val IntegerFive = new \n"
      + "   type T = system.Int \n"
      + "   def apply(): this.T \n"
      + "       5 \n\n"

      + "val IntegerTen = new \n"
      + "   type T = system.Int \n"
      + "   def apply(): this.T \n"
      + "       10 \n\n"

      + "ifSt(true, IntegerTen, IntegerFive)";
    testPyFromInput(input, "10", true);
  }
}
