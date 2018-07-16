package wyvern.tools.tests;

import org.junit.Before;
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
        Globals.resetPrelude();
    }*/

    @BeforeClass public static void setupResolver() {
        TestUtil.setPaths();
        WyvernResolver.getInstance().addPath(PATH);
    }

    @Test
    public void testSafeSQL() throws ParseException {
        TestUtil.doTestScriptModularly("modules.sqlMain", Util.intType(), new IntegerLiteral(5));
    }

    @Test
    public void testWebServer() throws ParseException {
        TestUtil.doTestScriptModularly("webarch.driver", Util.stringType(), new StringLiteral("ha"));
    }

    @Test
    public void testListClient() throws ParseException {
        TestUtil.doTestScriptModularly("demo.ListClient", Util.intType(), new IntegerLiteral(8));
    }


    @Test
    public void testSimpleDelegation() throws ParseException {
        String program = TestUtil.readFile(PATH + "SimpleDelegation.wyv");
        TestUtil.getNewAST(program, "test input");
        // TODO: implement delegation in the new IL and run this test
    }

}
