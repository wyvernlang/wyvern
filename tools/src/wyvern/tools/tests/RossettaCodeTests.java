package wyvern.tools.tests;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import wyvern.tools.imports.extensions.WyvernResolver;
import wyvern.tools.parsing.coreparser.ParseException;
import wyvern.tools.tests.suites.RegressionTests;

@Category(RegressionTests.class)
public class RossettaCodeTests {
    @BeforeClass public static void setupResolver() {
        TestUtil.setPaths();
        WyvernResolver.getInstance().addPath(PATH);
        //Globals.setUsePrelude(false);
    }

    @AfterClass public static void teardown() {
        //Globals.setUsePrelude(true);  // restore the default to use the prelude
    }

    private static final String BASE_PATH = TestUtil.BASE_PATH;
    private static final String PATH = BASE_PATH + "rosetta2/";

    @Test
    public void testInsertionSort() throws ParseException {
        TestUtil.doTestScriptModularly("rosetta.insertion-sort", null, null);
    }
}
