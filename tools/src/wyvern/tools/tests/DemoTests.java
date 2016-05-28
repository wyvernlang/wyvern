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
import wyvern.target.corewyvernIL.support.EvalContext;
import wyvern.target.corewyvernIL.support.GenContext;
import wyvern.target.corewyvernIL.support.GenUtil;
import wyvern.target.corewyvernIL.support.InterpreterState;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.type.ValueType;
import wyvern.tools.imports.extensions.WyvernResolver;
import wyvern.tools.parsing.coreparser.ParseException;
import wyvern.tools.tests.suites.RegressionTests;
import wyvern.tools.tests.tagTests.TestUtil;
import wyvern.tools.typedAST.abs.Declaration;
import wyvern.tools.typedAST.core.values.IntegerConstant;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;

public class DemoTests {
	private static final String BASE_PATH = TestUtil.BASE_PATH;
	private static final String PATH = BASE_PATH + "demo/";	
	
    @BeforeClass public static void setupResolver() {
    	TestUtil.setPaths();
		WyvernResolver.getInstance().addPath(PATH);
    }
    
	private static final String SQL_PATH = BASE_PATH + "safesql/";
	
	@Test
	@Category(RegressionTests.class)
	public void testSafeSqlDemo() throws ParseException {

		String[] fileList = {"StringSQL.wyt", "stringSQL.wyv", "SafeSQL.wyt", "safeSQL.wyv", "application.wyv", "sqlMain.wyv", "safeSQLDriver.wyv", };
		GenContext genCtx = Globals.getGenContext(new InterpreterState(new File(SQL_PATH), new File(TestUtil.LIB_PATH)));

		TypeContext ctx = Globals.getStandardTypeContext();
		//GenContext genCtx = GenContext.empty().extend("system", new Variable("system"), new NominalType("", "system"));
		
		List<wyvern.target.corewyvernIL.decl.Declaration> decls = new LinkedList<wyvern.target.corewyvernIL.decl.Declaration>();
		
		for(String fileName : fileList) {
			System.out.println(fileName);
			String source = TestUtil.readFile(SQL_PATH + fileName);
			TypedAST ast = TestUtil.getNewAST(source);
			wyvern.target.corewyvernIL.decl.Declaration decl = ((Declaration) ast).topLevelGen(genCtx, null);
			decls.add(decl);
			genCtx = GenUtil.link(genCtx, decl);
		}
		
		Expression mainProgram = GenUtil.genExp(decls, genCtx);
		// after genExp the modules are transferred into an object. We need to evaluate one field of the main object
		Expression program = new FieldGet(mainProgram, "x", null); 
		
		ValueType t = program.typeCheck(ctx);
		wyvern.target.corewyvernIL.expression.Value v = program.interpret(EvalContext.empty());
    	IntegerLiteral five = new IntegerLiteral(5);
		Assert.assertEquals(five, v);
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
	
}
