package wyvern.tools.tests;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import wyvern.target.corewyvernIL.expression.BooleanLiteral;
import wyvern.target.corewyvernIL.expression.IntegerLiteral;
import wyvern.target.corewyvernIL.support.Util;
import wyvern.tools.PythonCompiler;
import wyvern.tools.imports.extensions.WyvernResolver;
import wyvern.tools.parsing.coreparser.ParseException;
import wyvern.tools.tests.suites.CurrentlyBroken;
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
	public void testBox() throws ParseException {
		TestUtil.doTestScriptModularly(PATH, "introductory.box", Util.intType(), new IntegerLiteral(15));
	}
	
    @Test
    public void testFunctions() throws ParseException {
        TestUtil.doTestScriptModularly(PATH, "introductory.functions", Util.intType(), new IntegerLiteral(6));
    }
    
    @Test
    public void testObjects() throws ParseException {
        TestUtil.doTestScriptModularly(PATH, "introductory.objects", Util.intType(), new IntegerLiteral(7));
    }
    
    @Test
    public void testTailCalls() throws ParseException {
        TestUtil.doTestScriptModularly(PATH, "introductory.tailcalls", Util.intType(), new IntegerLiteral(10000));
    }
    
    @Test
    public void testStrings() throws ParseException {
        TestUtil.doTestScriptModularly(PATH, "introductory.strings", Util.booleanType(), new BooleanLiteral(true));
    }
    
    @Test
    public void testCell() throws ParseException {
        TestUtil.doTestScriptModularly(PATH, "introductory.cell", Util.intType(), new IntegerLiteral(3));
    }
    
    @Test
    public void testCellClient() throws ParseException {
        TestUtil.doTestScriptModularly(PATH, "modules.cellClient", Util.intType(), new IntegerLiteral(7));
    }
    
    @Test
    public void testCellModuleClient() throws ParseException {
        TestUtil.doTestScriptModularly(PATH, "modules.cellModuleClient", Util.intType(), new IntegerLiteral(2));
    }
    
    @Test
    public void testCellClientMain() throws ParseException {
        TestUtil.doTestScriptModularly(PATH, "modules.cellClientMain", Util.intType(), new IntegerLiteral(1));
    }
    
	@Test
	public void testOptionParameterized() throws ParseException {
		TestUtil.doTestScriptModularly(PATH, "introductory.optionP", Util.intType(), new IntegerLiteral(15));
	}

    @Test
    public void testPalindromeChecker() throws ParseException {
	TestUtil.doTestScriptModularly(PATH, "rosetta/check-palindrome", Util.unitType(), Util.unitValue());
    }
    
    @Test
    @Category(CurrentlyBroken.class)
    public void testListParameterized() throws ParseException {
        TestUtil.doTestScriptModularly(PATH, "introductory.listP", Util.intType(), new IntegerLiteral(15));
    }
    
    @Test
    public void testJavaFFI() throws ParseException {
        TestUtil.doTestScriptModularly(PATH, "ffi.callFromJava", Util.unitType(), Util.unitValue());
    }
    
	@Test
	public void testCrossPlatformHello() throws ParseException {
		TestUtil.doTestScriptModularly(PATH, "xplatform.hello-via-writer", Util.unitType(), Util.unitValue());
	}
	
	@Test
	public void testExplicitCrossPlatformHello() throws ParseException {
		TestUtil.doTestScriptModularly(PATH, "xplatform.hello-explicit-writer", Util.unitType(), Util.unitValue());
	}
	
	@Test
	public void testPythonCompilerOnScript() {
		String[] args = new String[] { TestUtil.EXAMPLES_PATH + "pong/pong.wyv" };
		PythonCompiler.wyvernHome.set("..");
		PythonCompiler.wyvernRoot.set(TestUtil.EXAMPLES_PATH + "pong/");
		PythonCompiler.main(args);
	}
}
