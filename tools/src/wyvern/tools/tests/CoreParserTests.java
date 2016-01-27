package wyvern.tools.tests;

import java.io.Reader;
import java.io.StringReader;
import java.util.Optional;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import wyvern.stdlib.Globals;
import wyvern.tools.parsing.coreparser.ParseException;
import wyvern.tools.parsing.coreparser.ParseUtils;
import wyvern.tools.parsing.coreparser.WyvernParser;
import wyvern.tools.tests.suites.RegressionTests;
import wyvern.tools.tests.tagTests.TestUtil;
import wyvern.tools.typedAST.core.values.IntegerConstant;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.Int;

@Category(RegressionTests.class)
public class CoreParserTests {
    @BeforeClass public static void setupResolver() {
    	TestUtil.setPaths();
    }

	@Test
	public void testIdentity() throws ParseException {
		String input = "fn x : Int => x";
		Reader r = new StringReader(input);
		//TokenManager tm = new WyvernTokenManager(r, "test input");
		WyvernParser<TypedAST,Type> wp = ParseUtils.makeParser("test input", r);//new WyvernParser(tm);
		wp.Expression(null);
	}
	
	@Test
	public void testIdentityApp() throws ParseException {
		String input = "(fn x : Int => x)(3)";
		Reader r = new StringReader(input);
		//TokenManager tm = new WyvernTokenManager(r, "test input");
		//WyvernParser wp = new WyvernParser(tm);
		WyvernParser<TypedAST,Type> wp = ParseUtils.makeParser("test input", r);
		TypedAST testAST = wp.Expression(null);
		Type resultType = testAST.typecheck(Globals.getStandardEnv(), Optional.<Type>empty());
		Assert.assertEquals(resultType, new Int());
		Value out = testAST.evaluate(Globals.getStandardEvalEnv());
		int finalRes = ((IntegerConstant)out).getValue();
		Assert.assertEquals(3, finalRes);
	}
	
	@Test
	public void testValVar() throws ParseException {
		String input = "require stdout\n\n"
					 + "val x = \"Hello, \"\n"
					 + "var y : Str = \"World\"\n"
					 + "val z : Str = \"!\"\n"
				     + "stdout.print(x)\n"
				     + "stdout.print(y)\n"
				     + "stdout.print(z)\n";
		TypedAST ast = TestUtil.getNewAST(input);
		TestUtil.evaluateNew(ast);
	}
	
	@Test
	public void testNewInvoke() throws ParseException {
		String input = "val obj = new\n"
				     + "    def getValue():Int\n"
				     + "        5\n"
				     + "obj.getValue()\n"
				     ;
		TypedAST ast = TestUtil.getNewAST(input);
		Value out = TestUtil.evaluateNew(ast);
		int finalRes = ((IntegerConstant)out).getValue();
		Assert.assertEquals(5, finalRes);
	}
	
	@Test
	public void testFieldRead() throws ParseException {
		String input = "val obj = new\n"
				     + "    val v:Int = 5\n"
				     + "obj.v\n"
				     ;
		TypedAST ast = TestUtil.getNewAST(input);
		Value out = TestUtil.evaluateNew(ast);
		int finalRes = ((IntegerConstant)out).getValue();
		Assert.assertEquals(5, finalRes);
	}
	@Test
	public void testVarField() throws ParseException {
		String input = "val obj = new\n"
				     + "    var v:Int = 5\n"
				     + "obj.v = 3\n"
				     + "obj.v\n"
				     ;
		TypedAST ast = TestUtil.getNewAST(input);
		Value out = TestUtil.evaluateNew(ast);
		int finalRes = ((IntegerConstant)out).getValue();
		Assert.assertEquals(3, finalRes);
	}
	
	@Test
	public void testTypeDecl() throws ParseException {
		String input = ""
			         + "type ValHolder\n"
				     + "    def getValue():Int\n"
				     + "val obj : ValHolder = new\n"
				     + "    def getValue():Int\n"
				     + "        5\n"
				     + "obj.getValue()\n"
				     ;
		TypedAST ast = TestUtil.getNewAST(input);
		Value out = TestUtil.evaluateNew(ast);
		int finalRes = ((IntegerConstant)out).getValue();
		Assert.assertEquals(5, finalRes);
	}	
	
}
