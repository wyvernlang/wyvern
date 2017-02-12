package wyvern.tools.tests;

import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import edu.umn.cs.melt.copper.runtime.logging.CopperParserException;
import wyvern.tools.imports.extensions.WyvernResolver;
import wyvern.tools.parsing.coreparser.ParseException;
import wyvern.tools.tests.suites.CurrentlyBroken;
import wyvern.tools.tests.suites.RegressionTests;
import wyvern.tools.tests.TestUtil;
import wyvern.tools.typedAST.interfaces.TypedAST;

@Category(RegressionTests.class)
public class RossettaCodeTests {
    @BeforeClass public static void setupResolver() {
    	TestUtil.setPaths();
		WyvernResolver.getInstance().addPath(PATH);
    }

	private static final String BASE_PATH = TestUtil.BASE_PATH;
	private static final String PATH = BASE_PATH + "rosetta2/";
	
	@Test
	public void testInsertionSort() throws ParseException {
		TestUtil.doTestScriptModularly("rosetta.insertion-sort", null, null);
	}
}
