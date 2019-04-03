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
import wyvern.tools.tests.suites.RegressionTests;

import static wyvern.tools.tests.TestUtil.WYVERN_HOME;

/**
 * Test suite for polymorphic effects with higher order functions
 */
@Category(RegressionTests.class)
public class FileTest {
    public static final String PATH = WYVERN_HOME == null
            ? "src/wyvern/tools/tests/" : WYVERN_HOME + "/tools/src/wyvern/tools/tests/java/";

    @BeforeClass
    public static void setupResolver() {
        TestUtil.setPaths();
        WyvernResolver.getInstance().addPath(PATH);
    }

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void script() throws ParseException {
        TestUtil.doTestScriptModularly(PATH, "java.script", Util.stringType(), new StringLiteral("abc"));
    }


}
