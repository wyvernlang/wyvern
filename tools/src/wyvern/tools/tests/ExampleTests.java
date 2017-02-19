package wyvern.tools.tests;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import wyvern.target.corewyvernIL.expression.IntegerLiteral;
import wyvern.target.corewyvernIL.support.Util;
import wyvern.tools.PythonCompiler;
import wyvern.tools.imports.extensions.WyvernResolver;
import wyvern.tools.parsing.coreparser.ParseException;
import wyvern.tools.tests.suites.RegressionTests;

/** Runs the Wyvern compiler on the example source code in the wyvern/examples directory tree
 * 
 * @author aldrich
 *
 */
@Category(RegressionTests.class)
public class ExampleTests {
    private static final String PATH = TestUtil.EXAMPLES_PATH;

    @BeforeClass public static void setupResolver() {
        TestUtil.setPaths();
        WyvernResolver.getInstance().addPath(PATH);
    }

    @Test
	public void testHello() throws ParseException {
		TestUtil.doTestScriptModularly(PATH, "rosetta.hello", Util.unitType(), Util.unitValue());
	}
    
    @Test
	public void testHelloExplicit() throws ParseException {
		TestUtil.doTestScriptModularly(PATH, "rosetta.hello-explicit", Util.unitType(), Util.unitValue());
	}
    
	@Test
	public void testFib() throws ParseException {
		TestUtil.doTestScriptModularly(PATH, "rosetta.fibonacci", Util.unitType(), Util.unitValue());
	}

	@Test
	public void testFactorial() throws ParseException {
		TestUtil.doTestScriptModularly(PATH, "rosetta.factorial", Util.unitType(), Util.unitValue());
	}
	
	@Test
	public void testTSL() throws ParseException {
		TestUtil.doTestScriptModularly(PATH, "tsls.postfixClient", Util.intType(), new IntegerLiteral(7));
	}
	
	@Test
	public void testCrossPlatformHello() throws ParseException {
		TestUtil.doTestScriptModularly(PATH, "platform.hello-via-writer", Util.unitType(), Util.unitValue());
	}
	
	@Test
	public void testPythonCompilerOnScript() {
		String[] args = new String[] { TestUtil.EXAMPLES_PATH + "pong/pong.wyv" };
		PythonCompiler.wyvernHome.set("..");
		PythonCompiler.wyvernRoot.set(TestUtil.EXAMPLES_PATH + "pong/");
		PythonCompiler.main(args);
	}
	
}
