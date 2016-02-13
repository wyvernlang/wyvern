package wyvern.tools.tests;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import wyvern.tools.imports.extensions.WyvernResolver;
import wyvern.tools.parsing.coreparser.ParseException;
import wyvern.tools.tests.suites.RegressionTests;
import wyvern.tools.tests.tagTests.TestUtil;
import wyvern.tools.typedAST.core.values.IntegerConstant;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;

public class DemoTests {
    @BeforeClass public static void setupResolver() {
    	TestUtil.setPaths();
		WyvernResolver.getInstance().addPath(PATH);
    }

	@Test
	@Category(RegressionTests.class)
	public void testArithmeticAST() throws ParseException {
		String program = TestUtil.readFile(PATH + "ArithmeticAST.wyv");
		TypedAST ast = TestUtil.getNewAST(program);
		Value out = TestUtil.evaluateNew(ast);
		int finalRes = ((IntegerConstant)out).getValue();
		Assert.assertEquals(2, finalRes);
	}
	
	@Test
	public void testCalculatorTokens() throws ParseException {
		String program = TestUtil.readFile(PATH + "CalculatorTokens.wyv");
		TypedAST ast = TestUtil.getNewAST(program);
		Value out = TestUtil.evaluateNew(ast);
		int finalRes = ((IntegerConstant)out).getValue();
		Assert.assertEquals(3, finalRes);
	}
	
	@Test
	@Category(RegressionTests.class)
	public void testSimpleDelegation() throws ParseException {
		String program = TestUtil.readFile(PATH + "SimpleDelegation.wyv");
		TypedAST ast = TestUtil.getNewAST(program);
		//Value out = TestUtil.evaluateNew(ast);
		//int finalRes = ((IntegerConstant)out).getValue();
		//Assert.assertEquals(3, finalRes);
	}
	
	private static final String BASE_PATH = TestUtil.BASE_PATH;
	private static final String PATH = BASE_PATH + "demo/";	
}
