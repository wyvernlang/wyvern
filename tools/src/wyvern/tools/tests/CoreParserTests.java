package wyvern.tools.tests;

import static java.util.Optional.empty;
import static wyvern.stdlib.Globals.getStandardEnv;

import java.io.Reader;
import java.io.StringReader;
import java.util.Optional;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import wyvern.stdlib.Globals;
import wyvern.tools.imports.extensions.WyvernResolver;
import wyvern.tools.parsing.coreparser.ParseException;
import wyvern.tools.parsing.coreparser.ParseUtils;
import wyvern.tools.parsing.coreparser.TokenManager;
import wyvern.tools.parsing.coreparser.WyvernParser;
import wyvern.tools.parsing.coreparser.WyvernTokenManager;
import wyvern.tools.tests.tagTests.TestUtil;
import wyvern.tools.typedAST.core.values.IntegerConstant;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.Int;

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
		wp.Expression();
	}
	
	@Test
	public void testIdentityApp() throws ParseException {
		String input = "(fn x : Int => x)(3)";
		Reader r = new StringReader(input);
		//TokenManager tm = new WyvernTokenManager(r, "test input");
		//WyvernParser wp = new WyvernParser(tm);
		WyvernParser<TypedAST,Type> wp = ParseUtils.makeParser("test input", r);
		TypedAST testAST = wp.Expression();
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
				     + "stdout.print(z)";
		TypedAST ast = TestUtil.getNewAST(input);
		TestUtil.evaluateNew(ast);
	}
	
	@Test
	public void testNewInvoke() throws ParseException {
		String input = "";
	}
}
