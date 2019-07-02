package wyvern.tools.tests;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import wyvern.stdlib.Globals;
import wyvern.target.corewyvernIL.expression.IntegerLiteral;
import wyvern.target.corewyvernIL.support.Util;
import wyvern.tools.PythonCompiler;
import wyvern.tools.imports.extensions.WyvernResolver;
import wyvern.tools.parsing.coreparser.ParseException;
import wyvern.tools.tests.suites.RegressionTests;

/**
 * Runs the Wyvern compiler on the example source code in the wyvern/examples
 * directory tree
 *
 * @author aldrich
 *
 */
@Category(RegressionTests.class)
public class ExampleTests {
  private static final String PATH = TestUtil.EXAMPLES_PATH;

  @Before
  public void setup() {
    Globals.resetState();
  }

  @BeforeClass
  public static void setupResolver() {
    TestUtil.setPaths();
    WyvernResolver.getInstance().addPath(PATH);
  }

  @Test
  public void testFib() throws ParseException {
    TestUtil.doTestScriptModularly(PATH, "rosetta.fibonacci", Util.unitType(),
        Util.unitValue());
  }

  @Test
  public void testFactorial() throws ParseException {
    TestUtil.doTestScriptModularly(PATH, "rosetta.factorial", Util.unitType(),
        Util.unitValue());
  }

  @Test
  public void testLinkedList() throws ParseException {
    TestUtil.doTestScriptModularly(PATH, "rosetta.linkedList", Util.intType(),
        Util.intValue(3));
  }

  @Test
  public void testTSL() throws ParseException {
    TestUtil.doTestScriptModularly(PATH, "tsls.postfixClient", Util.intType(),
        new IntegerLiteral(7));
  }

  @Test
  public void testAlgebra() throws ParseException {
    TestUtil.doTestScriptModularly(PATH, "introductory.algebra",
        Util.unitType(), Util.unitValue());
  }

  @Test
  public void testTailCalls() throws ParseException {
    TestUtil.doTestScriptModularly(PATH, "introductory.tailcalls",
        Util.intType(), new IntegerLiteral(10000));
  }

  @Test
  public void testPalindromeChecker() throws ParseException {
    TestUtil.doTestScriptModularly(PATH, "rosetta.check-palindrome",
        Util.unitType(), Util.unitValue());
  }

  @Test
  public void testMultiLambda() throws ParseException {
    TestUtil.doTestScriptModularly(PATH, "multiLambda",
        Util.unitType(), Util.unitValue());
  }

  @Test
  public void testListParameterized() throws ParseException {
    TestUtil.doTestScriptModularly(PATH, "introductory.listClient",
        Util.intType(), new IntegerLiteral(28));
  }

  @Test
  public void testLambdaCalculus() throws ParseException {
    TestUtil.doTestScriptModularly(PATH, "introductory.lambdaCalculusToJS",
        null, null);
  }

  @Test
  public void testCaretaker() throws ParseException {
    TestUtil.doTestScriptModularly(PATH, "capabilities.Caretaker", null, null);
  }

  @Test
  public void testSealerUnsealer() throws ParseException {
    TestUtil.doTestScriptModularly(PATH, "capabilities.SealerUnsealer", null,
        null);
  }

  @Test
  public void testPythonCompilerOnScript() {
    String[] args = new String[] {TestUtil.EXAMPLES_PATH + "pong/pong.wyv"};
    PythonCompiler.wyvernHome.set("..");
    PythonCompiler.wyvernRoot.set(TestUtil.EXAMPLES_PATH + "pong/");
    PythonCompiler.main(args);
  }

  @Test
  public void testPython2Webserver() {
    String[] args = new String[] {
        TestUtil.EXAMPLES_PATH + "web-server/python2/webserver.wyv"};
    PythonCompiler.wyvernHome.set("..");
    PythonCompiler.wyvernRoot
        .set(TestUtil.EXAMPLES_PATH + "web-server/python2/");
    PythonCompiler.main(args);
  }

  @Test
  public void testPython3Webserver() {
    String[] args = new String[] {
        TestUtil.EXAMPLES_PATH + "web-server/python3/webserver.wyv"};
    PythonCompiler.wyvernHome.set("..");
    PythonCompiler.wyvernRoot
        .set(TestUtil.EXAMPLES_PATH + "web-server/python3/");
    PythonCompiler.main(args);
  }

  @Test
  public void testBinarySearchTree() throws ParseException {
    TestUtil.doTestScriptModularly(PATH, "dataStructures.bsttest",
        Util.unitType(), Util.unitValue());
  }

  @Test
  public void testMandelbrot() throws ParseException {
    TestUtil.doTestScriptModularly(PATH, "rosetta.mandelbrot", Util.unitType(),
        Util.unitValue());
  }

  @Test
  public void testJulia() throws ParseException {
    TestUtil.doTestScriptModularly(PATH, "rosetta.julia", Util.unitType(),
        Util.unitValue());
  }

  @Test
  public void testThreads() throws ParseException {
    TestUtil.doTestScriptModularly(PATH, "threads.testThread", Util.unitType(),
        Util.unitValue());
  }
}
