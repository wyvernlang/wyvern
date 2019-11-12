package wyvern.tools.tests;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

import wyvern.target.corewyvernIL.expression.StringLiteral;
import wyvern.target.corewyvernIL.support.Util;
import wyvern.tools.imports.extensions.WyvernResolver;
import wyvern.tools.parsing.coreparser.ParseException;
import wyvern.tools.tests.suites.CurrentlyBroken;
import wyvern.tools.tests.suites.RegressionTests;



/**
 * Test suite for polymorphic effects with higher order functions
 */
@Category(RegressionTests.class)
public class FileTest {
    public static final String PATH = "../stdlib/platform/java/";
    public static final String PATH2 = TestUtil.BASE_PATH;


    @BeforeClass
    public static void setupResolver() {
        TestUtil.setPaths();
        WyvernResolver.getInstance().addPath(PATH);
    }

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void stdlib() throws ParseException {
        TestUtil.doTestScriptModularly(PATH, "script", Util.stringType(), new StringLiteral("abc"));
    }

    @Test
    @Category(CurrentlyBroken.class)
    public void editor() throws ParseException {
        TestUtil.doTestScriptModularly(PATH2, "editor.main", Util.stringType(), new StringLiteral("abc"));
    }
}
