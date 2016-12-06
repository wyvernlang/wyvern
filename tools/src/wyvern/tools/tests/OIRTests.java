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

import org.junit.Assert;
import wyvern.stdlib.Globals;
import wyvern.target.corewyvernIL.Environment;
import wyvern.target.corewyvernIL.astvisitor.EmitOIRVisitor;
import wyvern.target.corewyvernIL.astvisitor.EmitOIRState;
import wyvern.target.corewyvernIL.astvisitor.TailCallVisitor;
import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.expression.IExpr;
import wyvern.target.corewyvernIL.expression.Value;
import wyvern.target.corewyvernIL.modules.TypedModuleSpec;
import wyvern.target.corewyvernIL.support.EvalContext;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.support.InterpreterState;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.oir.OIRAST;
import wyvern.target.oir.OIREnvironment;
import wyvern.target.oir.EmitPythonVisitor;
import wyvern.tools.imports.extensions.WyvernResolver;
import wyvern.tools.parsing.coreparser.ParseException;
import wyvern.tools.tests.suites.CurrentlyBroken;
import wyvern.tools.tests.suites.RegressionTests;
import wyvern.tools.tests.tagTests.TestUtil;
import wyvern.tools.typedAST.interfaces.ExpressionAST;

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
    ExpressionAST ast = (ExpressionAST) TestUtil.getNewAST(input, "test input");
    InterpreterState state = new InterpreterState(InterpreterState.PLATFORM_PYTHON,
                                                  new File(TestUtil.BASE_PATH),
                                                  new File(TestUtil.LIB_PATH));
    GenContext javaGenContext = Globals.getStandardGenContext();
    GenContext pythonGenContext = Globals.getGenContext(state);
    LinkedList<TypedModuleSpec> dependencies = new LinkedList<>();
    IExpr ILprogram = ast.generateIL(pythonGenContext, null, dependencies);
    ILprogram = state.getResolver().wrap(ILprogram, dependencies);
    TailCallVisitor.annotate(ILprogram);
    
    if (debug) {
      System.out.println("Wyvern Program:");
      System.out.println(input);
      // IExpr jILprogram = ast.generateIL(javaGenContext, null, new LinkedList<TypedModuleSpec>());
      // TailCallVisitor.annotate(jILprogram);
      try {
        System.out.println("IL program:\n" + ((Expression)ILprogram).prettyPrint());
      } catch (IOException e) {
        System.err.println("Error pretty-printing IL program.");
      }
      // System.out.println("IL program output:\n" + jILprogram.interpret(EvalContext.empty()));
    }
    OIRAST oirast =
      ILprogram.acceptVisitor(new EmitOIRVisitor(),
                              new EmitOIRState(Globals.getStandardTypeContext(),
                                               OIREnvironment.getRootEnvironment()));

    String pprint =
      new EmitPythonVisitor().emitPython(oirast,
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
      Assert.fail("Error running python test: " + e.toString());
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
    testPyFromInput(input, "10");
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
  @Category(CurrentlyBroken.class)
  public void testImportsWithPython() throws ParseException {
	    String input =
	    	      "import python.iso\n\n" +
	    	      "iso.Color(\"green\")\n";
   	    testPyFromInput(input, "17");
  }
  
  @Test
  public void testDefDecl() throws ParseException {
    String input =
      "val obj = new\n" +
      "    val v : system.Int = 5\n" +
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
      "    def identity(x : Unit) : Unit = x\n" +
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

      + "val True = new \n"
      + "    def iff(thenFn: Body, elseFn: Body): thenFn.T \n\n"
      + "        thenFn.apply()\n\n"

      + "val False = new \n"
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

      + "ifSt(True, IntegerTen, IntegerFive)";
    testPyFromInput(input, "10");
  }

  @Test
  public void testArithmetic() throws ParseException {
    String input =
      "val x = ((5 + 3) / 2) * 2 - 1\n" +
      "x\n";
    testPyFromInput(input, "7");
  }

  @Test
  public void testNameCollision() throws ParseException {
    String input =
      "val letFn0 = 3\n" +
      "val x = 5\n" +
      "x + letFn0\n";
    testPyFromInput(input, "8");
  }

    @Test
    public void testNameCollision2() throws ParseException {
        String input =
            "val X = 3\n" +
            "val x = 5\n" +
            "x + X\n";
        testPyFromInput(input, "8");
    }

    @Test
    public void testNameCollision3() throws ParseException {
        String input =
            "val x = 7\n" +
            "val x = x\n" +
            "x\n";
        testPyFromInput(input, "7");
    }

    @Test
    public void testThisAsLocal() throws ParseException {
        String input =
            "val this = 3\n" +
            "this\n";
        testPyFromInput(input, "3");
    }

    @Test
    public void testBooleans() throws ParseException {
        String input =
            "val n = 5\n" +
            "(n < 2).ifTrue(() => 1, () => 2)\n";
        testPyFromInput(input, "2");
    }

    @Test
    public void testNotBooleans() throws ParseException {
        // Ensure that we don't translate "ifTrue" methods
        // on non-boolean objects
        String input =
            "val obj = new\n" +
            "  def ifTrue(x : Int, y : Int) : Int = x\n" +
            "obj.ifTrue(2,3)\n";
        testPyFromInput(input, "2");
    }

    @Test
    public void testRecursion() throws ParseException {
        String input =
            "def sum1ToN(n : Int) : Int\n" +
            "  (n < 2).ifTrue(\n" +
            "    () => 1,\n" +
            "    () => n + sum1ToN(n-1)\n" +
            "  )\n" +
            "sum1ToN(5)\n";
        testPyFromInput(input, "15");
    }

    @Test
    public void testFreeVariableTransitivity() throws ParseException {
        String input =
            "val x = 5\n" +
            "def f() : Int\n" +
            "  def g() : Int\n" +
            "    x + x\n" +
            "  g()\n" +
            "f()\n";
        testPyFromInput(input, "10");
    }

    @Test
    public void testCounter() throws ParseException {
        String input =
            "val counter = new\n" +
            "  var count : Int = 0\n" +
            "  def incr() : Int\n" +
            "    this.count = this.count + 1\n" +
            "    this.count\n" +
            "counter.incr()\n" +
            "counter.incr()\n";
        testPyFromInput(input, "2");
    }

    @Test
    public void testTCOShadowing() throws ParseException {
        String input =
            "def f() : Int\n" +
            "  def f() : Int = 7\n" +
            "  f()\n" +
            "f()\n";
        testPyFromInput(input, "7");
    }

    @Test
    public void testTCO() throws ParseException {
        String input =
            "def f(n : Int) : Int\n" +
            "  (n < 0).ifTrue(\n" +
            "    () => 1,\n" +
            "    () => f(n-1)\n" +
            "  )\n" +
            "f(50000)\n";
        testPyFromInput(input, "1");
    }

    @Test
    public void testBooleanLiterals() throws ParseException {
        String input =
            "(true).ifTrue(\n" +
            "  () => 1,\n" +
            "  () => 0)\n";
        testPyFromInput(input, "1");
    }

    @Test
    public void testImperativeIf() throws ParseException {
        String input =
            "(5 > 3).ifTrue(\n" +
            "  () => 1,\n" +
            "  () => 0)\n" +
            "7\n";
        testPyFromInput(input, "7");
    }

    @Test
    public void testEquality() throws ParseException {
        String input =
            "val x = 7\n" +
            "val a : Int = (x == 7).ifTrue(\n" +
            "  () => 3,\n" +
            "  () => 2)\n" +
            "val b : Int = (x == 13).ifTrue(\n" +
            "  () => 5,\n" +
            "  () => 0)\n" +
            "a + b\n";
        testPyFromInput(input, "3");
    }

    @Test
    public void testBooleanAnd() throws ParseException {
        String input =
            "true && false\n";
        testPyFromInput(input, "False");
    }

    @Test
    public void testBooleanOr() throws ParseException {
        String input =
            "true || false\n";
        testPyFromInput(input, "True");
    }

    @Test
    @Category(CurrentlyBroken.class)
    public void testNestedLambda() throws ParseException {
        String input =
            "val obj = new\n" +
            "  val x = 5\n" +
            "  val f = () => (() => this.x)()\n" +
            "obj.f()\n";
        testPyFromInput(input, "5");
    }

    @Test
    public void testNegativeInt() throws ParseException {
        String input =
            "-5\n";
        testPyFromInput(input, "-5");
    }

    @Test
    public void testNegativeInt2() throws ParseException {
        String input =
            "def f() : Int\n" +
            "  -5\n" +
            "f()\n";
        testPyFromInput(input, "-5");
    }

    @Test
    public void testReturnNew() throws ParseException {
        String input =
            "type T\n" +
            "  val x : Int\n" +
            "def makeT() : T = new\n" +
            "  val x = 23\n" +
            "makeT().x\n";
        testPyFromInput(input, "23");
    }

    @Test
    public void testTailCall() throws ParseException {
        String input =
            "def f() : Int\n" +
            "  3\n" +
            "def g() : Int\n" +
            "  7\n" +
            "  f()\n" +
            "g()\n";
        testPyFromInput(input, "3");
    }

    @Test
    public void testLiteralNewline() throws ParseException {
        String input =
            "val x = \"line 1\\nline 2\"\n" +
            "x";
        testPyFromInput(input, "line 1\nline 2");
    }

    @Test
    public void testBasicStdout() throws ParseException {
        String input =
            "require stdout\n" +
            "stdout.print(\"Hello, world\")\n" +
            "stdout.println()\n" +
            "0";
        testPyFromInput(input, "Hello, world\n0");
    }

    @Test
    public void testPythonBuiltins() throws ParseException {
        String input =
            "require python\n" +
            "python.toString(3)\n";
        testPyFromInput(input, "3");
    }

    @Test
    public void testOption() throws ParseException {
        String input =
            "import wyvern.option\n" +
            "require stdout\n" +
            "val orElse : option.UnitToDyn = new\n" +
            "  def apply() : Dyn = 5\n" +
            "val v1 : Int = option.Some(3).getOrElse(orElse)\n" +
            "val v2 : Int = option.None().getOrElse(orElse)\n" +
            "stdout.printInt(v1+v2)\n" +
            "stdout.println()\n" +
            "v1 + v2\n";
        testPyFromInput(input, "8\n8");
    }

    @Test
    public void testTSLIf() throws ParseException {
        String input =
            "import metadata wyvern.IfTSL\n" +
            "IfTSL.doif(true, ~)\n" +
            "  then\n" +
            "    7\n" +
            "  else\n" +
            "    8"
            "\n";
        testPyFromInput(input, "7");
    }
}
