package wyvern.tools.tests;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import wyvern.stdlib.Globals;
import wyvern.target.corewyvernIL.expression.IntegerLiteral;
import wyvern.target.corewyvernIL.support.Util;
import wyvern.tools.Interpreter;
import wyvern.tools.PythonCompiler;
import wyvern.tools.errors.ToolError;
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
    public void testCellModuleClient() throws ParseException {
      TestUtil.doTestScriptModularly(PATH, "modules.cellModuleClient",
          Util.intType(), new IntegerLiteral(2));
    }

    @Test
    public void testHello() throws ParseException {
      TestUtil.doTestScriptModularly(PATH, "rosetta.hello", Util.unitType(),
          Util.unitValue());
    }

    @Test
    public void testCellClientMain() throws ParseException {
      TestUtil.doTestScriptModularly(PATH, "modules.cellClientMain",
          Util.intType(), new IntegerLiteral(1));
    }

    @Test
    public void testCrossPlatformHello() throws ParseException {
      TestUtil.doTestScriptModularly(PATH, "xplatform.hello-via-writer",
          Util.unitType(), Util.unitValue());
    }

    @Test
    public void testExplicitCrossPlatformHello() throws ParseException {
      TestUtil.doTestScriptModularly(PATH, "xplatform.hello-explicit-writer",
          Util.unitType(), Util.unitValue());
    }

    @Test
    public void testMembrane() throws ParseException {
      TestUtil.doTestScriptModularly(PATH, "capabilities.Membrane", null, null);
    }
    
    @Test
    public void testHelloExplicit() throws ParseException {
      TestUtil.doTestScriptModularly(PATH, "rosetta.hello-explicit",
          Util.unitType(), Util.unitValue());
    }

    @Test
    public void testFunctions() throws ParseException {
      TestUtil.doTestScriptModularly(PATH, "introductory.functions",
          Util.intType(), new IntegerLiteral(6));
    }

    @Test
    public void testIOLibServerClient() throws ParseException {
      ExecutorService executor = Executors.newFixedThreadPool(2);

      Future<?> futureServer = executor.submit(() -> {
        try {
          TestUtil.doTestScriptModularly(PATH, "io-lib-redesign.server", Util.unitType(),
              Util.unitValue());
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      });

      // We need to let the server start and get to waiting/blocking on a socket
      // with accept before we start client.
      // Thus I wait 20 seconds. The following code will also catch any ToolError
      // that might have happened in the test.

      try {
        futureServer.get(20, TimeUnit.SECONDS);
      } catch (InterruptedException e) {
        // This one is OK.
      } catch (TimeoutException e) {
        // This one is a good sign as it is waiting on a socket.
      } catch (ExecutionException e) {
        if (e.getCause().getCause() instanceof ToolError) {
          throw (ToolError) e.getCause().getCause();
        } else {
          throw new RuntimeException(e.getCause());
        }
      } finally {
        executor.shutdownNow();
      }

      // Client can now run and it will complete and shutdown the server as a
      // result if all goes well.
      try {
        TestUtil.doTestScriptModularly(PATH, "io-lib-redesign.client", Util.unitType(),
            Util.unitValue());
      } finally {
        // Just in case - we need to clean up.
        executor.shutdown();
        try {
          if (!executor.awaitTermination(3, TimeUnit.SECONDS)) {
            executor.shutdownNow();
          }
        } catch (InterruptedException e) {
        } finally {
          executor.shutdownNow();
        }
      }
    }

    @Test
    public void testCellClient() throws ParseException {
      TestUtil.doTestScriptModularly(PATH, "modules.cellClient", Util.intType(),
          new IntegerLiteral(7));
    }

    @Test
    public void testJavaFFI() throws ParseException {
      TestUtil.doTestScriptModularly(PATH, "ffi.callFromJava", Util.unitType(),
          Util.unitValue());
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

    @Test
    public void testTextEditorApplication() throws ParseException {
        TestUtil.doTestScriptModularly(PATH + "text-editor", "main", Util.unitType(), Util.unitValue(), false);
    }

    @Test
    public void testCommandLineArguments() throws ParseException {
        String[] args = new String[] {TestUtil.EXAMPLES_PATH + "commandLineArguments.wyv"};
        Interpreter.wyvernHome.set("..");
        Interpreter.main(args);
    }

    @Test
    public void testShortCircuitEvaluation() throws ParseException {
      TestUtil.doTestScriptModularly(PATH, "shortCircuitEval", Util.unitType(),
      Util.unitValue());
    }

    @Test
    public void testOptionTypeSugar() throws ParseException {
      TestUtil.doTestScriptModularly(PATH, "optionType", Util.unitType(),
      Util.unitValue());
    }
}
