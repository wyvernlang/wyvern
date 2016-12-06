package wyvern.tools.tests;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import wyvern.target.corewyvernIL.expression.IntegerLiteral;
import wyvern.target.corewyvernIL.support.Util;
import wyvern.tools.imports.extensions.WyvernResolver;
import wyvern.tools.parsing.coreparser.ParseException;
import wyvern.tools.tests.suites.RegressionTests;
import wyvern.tools.tests.tagTests.TestUtil;

@Category(RegressionTests.class)
public class Figures {
    	
	private static final String BASE_PATH = TestUtil.BASE_PATH;
	private static final String PATH = BASE_PATH + "figs/";
	
    @BeforeClass public static void setupResolver() {
    	TestUtil.setPaths();
		WyvernResolver.getInstance().addPath(PATH);
    }

	@Test
	public void testFigure2() throws ParseException {
		ILTests.doTestScriptModularly("figs.figure2driver",
				Util.intType(),
				new IntegerLiteral(5));
	}
	
	@Test
	public void testFigure3() throws ParseException {
		ILTests.doTestScriptModularly("figs.figure3driver",
				Util.intType(),
				new IntegerLiteral(5));
	}
	
	@Test
	public void testFigure4() throws ParseException {
		ILTests.doTestScriptModularly("figs.figure4driver",
				Util.intType(),
				new IntegerLiteral(5));
	}
	
}
