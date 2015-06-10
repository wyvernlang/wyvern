package wyvern.tools.tests;

import static java.util.Optional.empty;
import static wyvern.stdlib.Globals.getStandardEnv;

import java.io.Reader;
import java.io.StringReader;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Test;

import wyvern.stdlib.Globals;
import wyvern.tools.parsing.coreparser.ParseException;
import wyvern.tools.parsing.coreparser.TokenManager;
import wyvern.tools.parsing.coreparser.WyvernParser;
import wyvern.tools.parsing.coreparser.WyvernTokenManager;
import wyvern.tools.typedAST.core.values.IntegerConstant;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.Int;

public class CoreParserTests {
	@Test
	public void testIdentity() throws ParseException {
		String input = "fn x : Int => x";
		Reader r = new StringReader(input);
		TokenManager tm = new WyvernTokenManager(r, "test input");
		WyvernParser wp = new WyvernParser(tm);
		wp.Expression();
	}
	
	@Test
	public void testIdentityApp() throws ParseException {
		String input = "(fn x : Int => x)(3)";
		Reader r = new StringReader(input);
		TokenManager tm = new WyvernTokenManager(r, "test input");
		WyvernParser wp = new WyvernParser(tm);
		TypedAST testAST = wp.Expression();
		Type resultType = testAST.typecheck(Globals.getStandardEnv(), Optional.<Type>empty());
		Assert.assertEquals(resultType, new Int());
		Value out = testAST.evaluate(Globals.getStandardEvalEnv());
		int finalRes = ((IntegerConstant)out).getValue();
		Assert.assertEquals(3, finalRes);
	}
}
