package wyvern.tools.tests;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import wyvern.stdlib.Globals;
import wyvern.target.corewyvernIL.expression.IntegerLiteral;
import wyvern.target.corewyvernIL.expression.StringLiteral;
import wyvern.target.corewyvernIL.support.Util;
import wyvern.tools.imports.extensions.WyvernResolver;
import wyvern.tools.parsing.coreparser.ParseException;
import wyvern.tools.tests.suites.RegressionTests;

@Category(RegressionTests.class)
public class DemoTests {
    private static final String BASE_PATH = TestUtil.BASE_PATH;
    private static final String PATH = BASE_PATH + "demo/";

    /*@Before
    public void setup() {
        Globals.resetState();
    }*/

    @BeforeClass public static void setupResolver() {
        TestUtil.setPaths();
        WyvernResolver.getInstance().addPath(PATH);
        Globals.setUsePrelude(false);
    }

    @AfterClass public static void teardown() {
        Globals.setUsePrelude(true);  // restore the default to use the prelude
    }


    @Test
    public void testSafeSQL() throws ParseException {
        TestUtil.doTestScriptModularly("modules.sqlMain", Util.intType(), new IntegerLiteral(5));
    }

    @Test
    public void testWebServer() throws ParseException {
        Globals.resetState();
        Globals.setUsePrelude(true);
        TestUtil.doTestScriptModularly("webarch.driver", Util.stringType(), new StringLiteral("ha"));
        Globals.setUsePrelude(false);
    }

    @Test
    public void testListClient() throws ParseException {
        Globals.setUsePrelude(true);
        TestUtil.doTestScriptModularly("demo.ListClient", Util.intType(), new IntegerLiteral(8));
        Globals.setUsePrelude(false);
    }


    @Test
    public void testSimpleForward() throws ParseException {
        String program = TestUtil.readFile(PATH + "SimpleForwarding.wyv");
        TestUtil.getNewAST(program, "test input");
        // TODO: implement delegation in the new IL and run this test
    }

}
