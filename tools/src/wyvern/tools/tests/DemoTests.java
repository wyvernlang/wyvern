package wyvern.tools.tests;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import wyvern.stdlib.Globals;
import wyvern.target.corewyvernIL.expression.Expression;
import wyvern.target.corewyvernIL.expression.FieldGet;
import wyvern.target.corewyvernIL.expression.IntegerLiteral;
import wyvern.target.corewyvernIL.expression.StringLiteral;
import wyvern.target.corewyvernIL.support.EvalContext;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.support.GenUtil;
import wyvern.target.corewyvernIL.support.InterpreterState;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.support.Util;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.imports.extensions.WyvernResolver;
import wyvern.tools.parsing.coreparser.ParseException;
import wyvern.tools.tests.suites.CurrentlyBroken;
import wyvern.tools.tests.suites.RegressionTests;
import wyvern.tools.tests.tagTests.TestUtil;
import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.core.values.IntegerConstant;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;

@Category(RegressionTests.class)
public class DemoTests {
	private static final String BASE_PATH = TestUtil.BASE_PATH;
	private static final String PATH = BASE_PATH + "demo/";	
	
    @BeforeClass public static void setupResolver() {
    	TestUtil.setPaths();
		WyvernResolver.getInstance().addPath(PATH);
    }
    
	@Test
	public void testSafeSQL() throws ParseException {
		ILTests.doTestScriptModularly("modules.sqlMain", Util.intType(), new IntegerLiteral(5));
	}
	
	@Test
	public void testWebServer() throws ParseException {
		ILTests.doTestScriptModularly("webarch.driver", Util.stringType(), new StringLiteral("ha"));
	}
	
	@Test
	@Category(RegressionTests.class)
	public void testArithmeticAST() throws ParseException {
		String program = TestUtil.readFile(PATH + "ArithmeticAST.wyv");
		TypedAST ast = TestUtil.getNewAST(program, "test input");
		Value out = TestUtil.evaluateNew(ast);
		int finalRes = ((IntegerConstant)out).getValue();
		Assert.assertEquals(2, finalRes);
	}
	
	@Test
	@Category(CurrentlyBroken.class)
	public void testCalculatorTokens() throws ParseException {
		String program = TestUtil.readFile(PATH + "CalculatorTokens.wyv");
		TypedAST ast = TestUtil.getNewAST(program, "test input");
		Value out = TestUtil.evaluateNew(ast);
		int finalRes = ((IntegerConstant)out).getValue();
		Assert.assertEquals(3, finalRes);
	}
	
	@Test
	public void testSimpleDelegation() throws ParseException {
		String program = TestUtil.readFile(PATH + "SimpleDelegation.wyv");
		TypedAST ast = TestUtil.getNewAST(program, "test input");
		//Value out = TestUtil.evaluateNew(ast);
		//int finalRes = ((IntegerConstant)out).getValue();
		//Assert.assertEquals(3, finalRes);
	}
	
}
